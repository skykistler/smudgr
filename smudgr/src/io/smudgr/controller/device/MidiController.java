package io.smudgr.controller.device;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.midi.MidiMessage;

import io.smudgr.alg.Algorithm;
import io.smudgr.alg.param.Parameter;
import io.smudgr.controller.Controller;
import io.smudgr.controller.controls.Controllable;

public class MidiController extends Controller implements DeviceObserver {

	private Device input;
	private int channel;
	private HashMap<Integer, ArrayList<Controllable>> midiMap;
	private HashMap<Integer, MidiControlStrategy> strategies;

	private boolean waitingForKey = false;
	private int lastKeyPressed = -1;

	public MidiController(int channel) {
		midiMap = new HashMap<Integer, ArrayList<Controllable>>();
		strategies = new HashMap<Integer, MidiControlStrategy>();

		this.channel = channel;
		strategies.put(0x90, new NoteOnControl());
		strategies.put(0x80, new NoteOffControl());
		strategies.put(0xA0, new AftertouchControl());
		strategies.put(0xB0, new KnobControl());
	}

	public HashMap<Integer, MidiControlStrategy> getStrategies() {
		return strategies;
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

	public Device getInputDevice() {
		return input;
	}

	private void bindParameters() {
		ArrayList<Controllable> alreadyBound = new ArrayList<Controllable>();

		for (Algorithm a : getSmudge().getAlgorithms()) {
			for (Parameter p : a.getParameters()) {
				if (p.isBindRequested()) {
					System.out.println("Binding: " + p.getParent() + " - " + p + "... please touch a MIDI key...");
					bindControl(p);
					alreadyBound.add(p);
				}
			}
		}

		for (Controllable c : getControls()) {
			if (!alreadyBound.contains(c)) {
				if (c.isBindRequested()) {
					System.out.println("Please touch a MIDI key to bind: " + c);
					bindControl(c);
				}
			}
		}
	}

	private void bindControl(Controllable c) {
		lastKeyPressed = -1;
		waitingForKey = true;

		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		ArrayList<Controllable> controls = getKeyBinds(lastKeyPressed);
		if (controls == null) {
			midiMap.put(lastKeyPressed, new ArrayList<Controllable>());
			getKeyBinds(lastKeyPressed).add(c);
			waitingForKey = false;
		} else {
			bindControl(c);
		}
	}

	public ArrayList<Controllable> getKeyBinds(int key) {
		return midiMap.get(key);
	}

	public void midiInput(MidiMessage message) {
		synchronized (getSmudge()) {
			byte[] digest = message.getMessage();

			int status = message.getStatus();

			// If message isn't on our channel, skip it
			int message_channel = (status & 0xF) + 1;
			if (message_channel != channel)
				return;

			// If not a system message, clear channel for lookup
			if (status < 0xF0)
				status = (status >> 4) << 4;

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
				key = status;
				value = status;
			}

			lastKeyPressed = key;

			// If waiting for key to bind, wake thread to continue
			if (waitingForKey) {
				synchronized (this) {
					notify();
				}
			} else {
				ArrayList<Controllable> bound = getKeyBinds(key);

				if (bound != null)
					for (Controllable c : bound)
						strategies.get(status).input(c, value);
			}
		}
	}

}
