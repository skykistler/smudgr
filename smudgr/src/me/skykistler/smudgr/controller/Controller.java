package me.skykistler.smudgr.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.midi.MidiMessage;

import me.skykistler.smudgr.Smudge;
import me.skykistler.smudgr.alg.Algorithm;
import me.skykistler.smudgr.alg.param.Parameter;
import me.skykistler.smudgr.controller.controls.Controllable;
import me.skykistler.smudgr.controller.device.Device;
import me.skykistler.smudgr.controller.device.DeviceObserver;
import me.skykistler.smudgr.view.View;

public class Controller implements DeviceObserver {

	public static Controller startSmudge(Smudge s) {
		Controller c = new Controller();
		c.setSmudge(s);
		c.start();

		return c;
	}

	public static Controller startSmudge(Smudge s, String deviceName) {
		Controller c = new Controller();
		c.setSmudge(s);
		c.bindDevice(deviceName);
		c.start();

		return c;
	}

	private Smudge smudge;
	private View view;
	private Device input;
	private HashMap<Integer, ArrayList<Controllable>> midiMap;

	public Controller() {
		view = new View();
		midiMap = new HashMap<Integer, ArrayList<Controllable>>();
	}

	public void setSmudge(Smudge s) {
		smudge = s;
		view.setSmudge(smudge);
	}

	public void start() {
		view.init();
	}

	public void bindDevice(String deviceName) {
		input = new Device(this, deviceName);

		System.out.println("Bound to " + input);

		if (smudge == null)
			return;

		bindParameters();
	}

	public Device getInputDevice() {
		return input;
	}

	private void bindParameters() {
		ArrayList<Controllable> alreadyBound = new ArrayList<Controllable>();

		for (Algorithm a : smudge.getAlgorithms()) {
			for (Parameter p : a.getParameters()) {
				if (p.isBindRequested()) {
					System.out.println("Binding " + p + " for " + p.getParent() + "... please touch a MIDI key...");
					bindControl(p);
					alreadyBound.add(p);
				}
			}
		}

		for (Controllable c : Controllable.getControls()) {
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

		ArrayList<Controllable> controls = getBound(lastKeyPressed);
		if (controls == null) {
			midiMap.put(lastKeyPressed, new ArrayList<Controllable>());
			getBound(lastKeyPressed).add(c);
			waitingForKey = false;
		} else {
			bindControl(c);
		}
	}

	public ArrayList<Controllable> getBound(int key) {
		return midiMap.get(key);
	}

	private boolean waitingForKey = false;
	private int lastKeyPressed = -1;

	public void midiInput(MidiMessage message) {
		synchronized (smudge) {
			byte[] digest = message.getMessage();

			int status = digest[0];
			// If not control value, note on, or note off, skip it
			if (!(status == -80 || status == -112 || status == -128))
				return;

			if (!waitingForKey)
				if (message.getLength() == 3) {
					int key = digest[1];
					int value = digest[2];

					ArrayList<Controllable> bound = getBound(key);
					if (bound != null)
						for (Controllable c : bound) {
							// If control value change
							if (status == -80) {
								// Twist up
								if (value > 64)
									c.increment();
								// Twist down
								else if (value < 64)
									c.decrement();
							} else {
								// If note on
								if (status == -112)
									c.noteOn(key);

								// Velocity, or whatever else
								c.midiValue(value);

								// If note off
								if (status == -128)
									c.noteOff(key);
							}
						}
				}

			if (message.getLength() > 1)
				lastKeyPressed = digest[1];

			if (waitingForKey)
				synchronized (this) {
					notify();
				}
		}
	}

}
