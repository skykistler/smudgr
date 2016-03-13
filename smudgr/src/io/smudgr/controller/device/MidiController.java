package io.smudgr.controller.device;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.midi.MidiMessage;

import io.smudgr.controller.Controller;
import io.smudgr.controller.controls.Controllable;
import io.smudgr.controller.controls.TimingControl;
import io.smudgr.controller.device.messages.AftertouchMessage;
import io.smudgr.controller.device.messages.ContinueMessage;
import io.smudgr.controller.device.messages.KnobMessage;
import io.smudgr.controller.device.messages.MidiMessageStrategy;
import io.smudgr.controller.device.messages.NoteOffMessage;
import io.smudgr.controller.device.messages.NoteOnMessage;
import io.smudgr.controller.device.messages.ResetMessage;
import io.smudgr.controller.device.messages.StartMessage;
import io.smudgr.controller.device.messages.StopMessage;
import io.smudgr.controller.device.messages.TimingClockMessage;

public class MidiController extends Controller implements DeviceObserver {

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

	public MidiController() {
		this("midi.map");
	}

	public MidiController(String savedMap) {
		devices = new ArrayList<Device>();

		midiMap = new MidiControlMap(savedMap);

		messageStrategies = new HashMap<Integer, MidiMessageStrategy>();
		messageStrategies.put(0x90, new NoteOnMessage());
		messageStrategies.put(0x80, new NoteOffMessage());
		messageStrategies.put(0xA0, new AftertouchMessage());
		messageStrategies.put(0xB0, new KnobMessage());
		messageStrategies.put(0xFA, new StartMessage());
		messageStrategies.put(0xFB, new ContinueMessage());
		messageStrategies.put(0xFC, new StopMessage());
		messageStrategies.put(0xFF, new ResetMessage());

		timingControl = new TimingControl(this);
		timingCalculator = new TimingClockMessage();

		systemControls = new HashMap<Integer, Controllable>();
		systemControls.put(0xFA, timingControl);
		systemControls.put(0xFB, timingControl);
		systemControls.put(0xFC, timingControl);
		systemControls.put(0xFF, timingControl);
	}

	public void bindDevice(String deviceName) {
		Device d = new Device(this, deviceName);

		if (!d.toString().equals("no device")) {
			devices.add(d);
			System.out.println("Bound to " + devices.get(devices.size() - 1));
		}
	}

	public void start() {
		if (!parametersBound && devices.size() > 0)
			bindParameters();

		super.start();
	}

	private void bindParameters() {
		for (Controllable c : getControls())
			bindControl(c);

		midiMap.save();
		parametersBound = true;
	}

	private void bindControl(Controllable c) {
		if (!c.isBindRequested())
			return;

		if (midiMap.isSaved(c)) {
			System.out.println("Assigned " + c + " from saved map.");
			return;
		}

		System.out.println("Please touch a MIDI key to bind: " + c + " ...");

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

		Controllable assigned = midiMap.getKeyBind(lastChannel, lastKeyPressed);
		if (assigned == null) {
			midiMap.assign(lastChannel, lastKeyPressed, c);
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
			synchronized (getSmudge()) {
				// If it's a system message, check our manual system controls
				// list. Otherwise get the bind
				Controllable bound = system_message ? systemControls.get(status) : midiMap.getKeyBind(channel, key);

				if (bound != null)
					messageStrategies.get(status).input(bound, value);
			}
		}
	}

}
