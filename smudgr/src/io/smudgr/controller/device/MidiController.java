package io.smudgr.controller.device;

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

	private Device input;
	private int channel;
	private MidiControlMap midiMap;
	private HashMap<Integer, MidiMessageStrategy> messageStrategies;
	private HashMap<Integer, Controllable> systemControls;

	private TimingControl timingControl;
	private TimingClockMessage timingCalculator;

	private boolean waitingForKey = false;
	private int lastKeyPressed = -1;

	public MidiController(int channel) {
		this(channel, "midi.map");
	}

	public MidiController(int channel, String savedMap) {
		this.channel = channel;
		midiMap = new MidiControlMap("midi.map");

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
		input = new Device(this, deviceName);

		System.out.println("Bound to " + input);

		if (getSmudge() == null) {
			System.out.println("Set smudge before binding device");
			return;
		}

		bindParameters();
	}

	private void bindParameters() {
		for (Controllable c : getControls()) {
			bindControl(c);
		}

		midiMap.save();
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
		waitingForKey = true;

		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Controllable assigned = midiMap.getKeyBind(lastKeyPressed);
		if (assigned == null) {
			midiMap.assign(lastKeyPressed, c);
			waitingForKey = false;
		} else {
			bindControl(c);
		}
	}

	public void midiInput(MidiMessage message) {
		int status = message.getStatus();

		if (status == 0xF8) {
			// tick the timing calculator without overhead
			timingCalculator.input(timingControl, 0);
			return;
		}

		byte[] digest = message.getMessage();

		boolean system_message = status >= 0xF0;

		if (!system_message) {
			// If message isn't on our channel, skip it
			int message_channel = (status & 0xF) + 1;
			if (message_channel != channel)
				return;

			// clear channel for lookup
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

		if (key != -1) {
			lastKeyPressed = key;

			// If waiting for key to bind, wake thread to continue
			if (waitingForKey) {
				synchronized (this) {
					notify();
				}
			}
		}

		if (!waitingForKey) {
			synchronized (getSmudge()) {
				// If it's a system message, check our manual system controls list. Otherwise get the bind
				Controllable bound = system_message ? systemControls.get(status) : midiMap.getKeyBind(key);

				if (bound != null)
					messageStrategies.get(status).input(bound, value);
			}
		}
	}

}
