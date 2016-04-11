package io.smudgr.extensions.midi;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.midi.MidiMessage;

import io.smudgr.app.Controller;
import io.smudgr.app.controls.Controllable;
import io.smudgr.app.controls.TimingControl;
import io.smudgr.extensions.ControllerExtension;
import io.smudgr.extensions.midi.messages.AftertouchMessage;
import io.smudgr.extensions.midi.messages.ContinueMessage;
import io.smudgr.extensions.midi.messages.KnobMessage;
import io.smudgr.extensions.midi.messages.MidiMessageStrategy;
import io.smudgr.extensions.midi.messages.NoteOffMessage;
import io.smudgr.extensions.midi.messages.NoteOnMessage;
import io.smudgr.extensions.midi.messages.ResetMessage;
import io.smudgr.extensions.midi.messages.StartMessage;
import io.smudgr.extensions.midi.messages.StopMessage;
import io.smudgr.extensions.midi.messages.TimingClockMessage;
import io.smudgr.smudge.param.Parameter;

public class MidiExtension implements ControllerExtension, DeviceObserver {

	public String getName() {
		return "MIDI Extension";
	}

	private ArrayList<Device> devices;
	private MidiControlMap midiMap;
	private HashMap<Integer, MidiMessageStrategy> messageStrategies;
	private HashMap<Integer, Controllable> systemControls;

	private TimingControl timingControl;
	private TimingClockMessage timingCalculator;

	private boolean parametersBound = false;
	private boolean waitingForKey = false;
	private int lastChannel = -1;
	private int lastKeyPressed = -1;

	public MidiExtension() {
		devices = new ArrayList<Device>();
		midiMap = new MidiControlMap();
	}

	public void init() {
		messageStrategies = new HashMap<Integer, MidiMessageStrategy>();
		messageStrategies.put(0x90, new NoteOnMessage());
		messageStrategies.put(0x80, new NoteOffMessage());
		messageStrategies.put(0xA0, new AftertouchMessage());
		messageStrategies.put(0xB0, new KnobMessage());
		messageStrategies.put(0xFA, new StartMessage());
		messageStrategies.put(0xFB, new ContinueMessage());
		messageStrategies.put(0xFC, new StopMessage());
		messageStrategies.put(0xFF, new ResetMessage());

		timingControl = new TimingControl();
		Controller.getInstance().add(timingControl);
		timingCalculator = new TimingClockMessage();

		systemControls = new HashMap<Integer, Controllable>();
		systemControls.put(0xFA, timingControl);
		systemControls.put(0xFB, timingControl);
		systemControls.put(0xFC, timingControl);
		systemControls.put(0xFF, timingControl);

		if (!parametersBound && devices.size() > 0)
			bindParameters();
	}

	public void update() {

	}

	public void stop() {

	}

	public void bindDevice(String deviceName) {
		Device d = new Device(this, deviceName);

		if (!d.toString().equals("no device")) {
			devices.add(d);
			System.out.println("Bound to " + devices.get(devices.size() - 1));
		}
	}

	private void bindParameters() {
		for (Controllable c : Controller.getInstance().getControls())
			bindControl(c);

		parametersBound = true;
	}

	private void bindControl(Controllable c) {
		String name = c.toString();
		if (c instanceof Parameter)
			name = ((Parameter) c).getParent().getName() + " - " + name;

		if (midiMap.isBound(c)) {
			System.out.println("Assigned " + name + " from saved map.");
			return;
		}

		if (!c.isBindRequested())
			return;

		System.out.println("Please touch a MIDI key to bind: " + name + " ...");

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

		Controllable assigned = midiMap.getControl(lastChannel, lastKeyPressed);
		if (assigned == null) {
			midiMap.assign(c, lastChannel, lastKeyPressed);
			waitingForKey = false;
		} else {
			bindControl(c);
		}
	}

	public void midiInput(MidiMessage message, long timestamp) {
		int status = message.getStatus();

		if (status == 0xF8) {
			// tick the timing calculator without overhead
			timingCalculator.input(timingControl, 0);
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
		if (!messageStrategies.containsKey(status))
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
			if (waitingForKey)
				synchronized (this) {
					notify();
				}
		}

		if (!waitingForKey) {
			synchronized (Controller.getInstance().getSmudge()) {
				// If it's a system message, check our manual system controls
				// list. Otherwise get the bind
				Controllable bound = system_message ? systemControls.get(status) : midiMap.getControl(channel, key);

				if (bound != null)
					messageStrategies.get(status).input(bound, value);
			}
		}
	}

	public MidiControlMap getMidiMap() {
		return midiMap;
	}

}
