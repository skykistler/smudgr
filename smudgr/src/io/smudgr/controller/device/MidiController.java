package io.smudgr.controller.device;

import java.util.HashMap;

import javax.sound.midi.MidiMessage;

import io.smudgr.controller.Controller;
import io.smudgr.controller.controls.Controllable;

public class MidiController extends Controller implements DeviceObserver {

	private Device input;
	private int channel;
	private MidiControlMap midiMap;
	private HashMap<Integer, MidiControlStrategy> strategies;

	private boolean waitingForKey = false;
	private int lastKeyPressed = -1;

	public MidiController(int channel) {
		this(channel, "midi.map");
	}

	public MidiController(int channel, String savedMap) {
		midiMap = new MidiControlMap("midi.map");

		strategies = new HashMap<Integer, MidiControlStrategy>();
		this.channel = channel;

		strategies.put(0x90, new NoteOnStrategy());
		strategies.put(0x80, new NoteOffStrategy());
		strategies.put(0xA0, new AftertouchStrategy());
		strategies.put(0xB0, new KnobStrategy());
		strategies.put(0xF8, new TimingClockStrategy(this));
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
		synchronized (getSmudge()) {
			byte[] digest = message.getMessage();

			int status = message.getStatus();

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
			if (!strategies.containsKey(status))
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
				Controllable bound = midiMap.getKeyBind(key);

				if (system_message || bound != null)
					strategies.get(status).input(bound, value);
			}
		}
	}

}
