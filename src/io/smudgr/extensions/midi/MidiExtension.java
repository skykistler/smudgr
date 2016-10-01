package io.smudgr.extensions.midi;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.midi.MidiMessage;

import io.smudgr.app.Controllable;
import io.smudgr.app.Controller;
import io.smudgr.extensions.ControllerExtension;
import io.smudgr.extensions.automate.controls.AutomatorControl;
import io.smudgr.extensions.midi.Device.DeviceObserver;
import io.smudgr.extensions.midi.messages.AftertouchMessage;
import io.smudgr.extensions.midi.messages.ContinueMessage;
import io.smudgr.extensions.midi.messages.ControlChangeMessage;
import io.smudgr.extensions.midi.messages.MidiMessageStrategy;
import io.smudgr.extensions.midi.messages.NoteOffMessage;
import io.smudgr.extensions.midi.messages.NoteOnMessage;
import io.smudgr.extensions.midi.messages.ResetMessage;
import io.smudgr.extensions.midi.messages.StartMessage;
import io.smudgr.extensions.midi.messages.StopMessage;
import io.smudgr.extensions.midi.tcp.DeviceServer;
import io.smudgr.project.ProjectElement;
import io.smudgr.project.PropertyMap;
import io.smudgr.project.smudge.param.Parameter;

public class MidiExtension implements ControllerExtension, DeviceObserver {

	public String getName() {
		return "MIDI";
	}

	private ArrayList<Device> devices;
	private DeviceServer deviceServer;
	private MidiControlMap midiMap;
	private HashMap<Integer, MidiMessageStrategy> messageStrategies;

	private TimingCalculator timingCalculator;

	private boolean waitingForKey = false;
	private int lastChannel = -1;
	private int lastKeyPressed = -1;

	public MidiExtension() {
		devices = new ArrayList<Device>();
		midiMap = new MidiControlMap();
		messageStrategies = new HashMap<Integer, MidiMessageStrategy>();

		messageStrategies.put(0x90, new NoteOnMessage());
		messageStrategies.put(0x80, new NoteOffMessage());
		messageStrategies.put(0xA0, new AftertouchMessage());
		messageStrategies.put(0xB0, new ControlChangeMessage());
		messageStrategies.put(0xFA, new StartMessage());
		messageStrategies.put(0xFB, new ContinueMessage());
		messageStrategies.put(0xFC, new StopMessage());
		messageStrategies.put(0xFF, new ResetMessage());
	}

	public void init() {
		timingCalculator = new TimingCalculator();
	}

	public void update() {

	}

	public void stop() {

	}

	public void bindDevice(String deviceName, boolean startServer) {
		ArrayList<DeviceObserver> observers = new ArrayList<DeviceObserver>();
		observers.add(this);

		if (startServer) {
			if (deviceServer == null)
				deviceServer = new DeviceServer();

			observers.add(deviceServer);
		}

		Device d = new Device(deviceName, observers);

		if (!d.toString().equals("no device")) {
			devices.add(d);
			System.out.println("Bound to " + devices.get(devices.size() - 1));
		}
	}

	public void waitForBind(int control_id, boolean absolute, int ignoreKey) {
		if (devices.size() == 0)
			return;

		ProjectElement element = getProject().getElement(control_id);
		if (element == null || !(element instanceof Controllable)) {
			System.out.println("Unable to bind " + element);
		}

		Controllable control = (Controllable) element;

		String name = control.getName();
		if (control instanceof Parameter) {
			Parameter param = (Parameter) control;
			name = param.getParent().getName() + " - " + param.getName();
		}

		if (control instanceof AutomatorControl) {
			Parameter param = ((AutomatorControl) control).getParameter();

			if (param != null)
				name = control.getName() + " Automator on " + param.getParent().getName() + " - " + param.getName();
		}

		if (midiMap.hasBind(control_id)) {
			System.out.println("Already assigned control: " + name);
			return;
		}

		System.out.println("Please touch a key to bind: " + name);

		lastKeyPressed = -1;
		lastChannel = -1;
		waitingForKey = true;

		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		int assigned = midiMap.getBind(lastChannel, lastKeyPressed);
		if (lastKeyPressed != ignoreKey && assigned == -1) {
			midiMap.setBind(control_id, lastChannel, lastKeyPressed, absolute);
			waitingForKey = false;
		} else {
			// Something already bound at given input, so try again
			waitForBind(control_id, absolute, ignoreKey);
		}
	}

	public void midiInput(MidiMessage message) {
		int status = message.getStatus();

		if (status == 0xF8) {
			// tick the timing calculator without overhead
			timingCalculator.tick();
			return;
		}

		byte[] digest = message.getMessage();

		boolean system_message = status >= 0xF0;

		int channel = -1;
		if (!system_message) {
			channel = (status & 0xF) + 1;
			status = (status >> 4) << 4;
		}

		// If we don't have a strategy for this message, skip it
		if (!waitingForKey && !messageStrategies.containsKey(status))
			return;

		int key;
		int value;
		if (message.getLength() == 3) {
			key = digest[1];
			value = digest[2];
		} else if (message.getLength() > 1) {
			key = digest[1];
			value = key;
		} else {
			key = -1;
			value = -1;
		}

		if (key != -1 && channel != -1) {
			lastKeyPressed = key;
			lastChannel = channel;

			// If waiting for key to bind, wake thread to continue
			if (waitingForKey) {
				synchronized (this) {
					notify();
				}

				return;
			}
		}

		synchronized (Controller.getInstance()) {
			// If it's a system message, pass it along
			if (system_message) {
				messageStrategies.get(status).input(null, value);
			}
			// Otherwise get the bind
			else {
				ProjectElement bound = getProject().getElement(midiMap.getBind(channel, key));

				if (bound != null && bound instanceof Controllable) {
					MidiMessageStrategy ms = messageStrategies.get(status);

					synchronized (Controller.getInstance().getProject().getSmudge()) {
						// If this is a CC message, we need to check if this is
						// an
						// absolute or relative bind
						if (ms instanceof ControlChangeMessage) {
							ControlChangeMessage ccm = (ControlChangeMessage) ms;
							ccm.input((Controllable) bound, value, midiMap.isAbsoluteBind(channel, key));
						} else {
							ms.input((Controllable) bound, value);
						}
					}
				}
			}
		}
	}

	public void save(PropertyMap pm) {
		for (PropertyMap mapping : midiMap.getBinds())
			pm.add(mapping);
	}

	public void load(PropertyMap pm) {
		ArrayList<PropertyMap> mappings = pm.getChildren(MidiControlMap.PROPERTY_MAP_KEY);

		for (PropertyMap mapping : mappings) {
			int control = Integer.parseInt(mapping.getAttribute("control"));
			int channel = Integer.parseInt(mapping.getAttribute("channel"));
			int key = Integer.parseInt(mapping.getAttribute("key"));
			boolean absolute = mapping.hasAttribute("absolute");

			midiMap.setBind(control, channel, key, absolute);
		}
	}

}
