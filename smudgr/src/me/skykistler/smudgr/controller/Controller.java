package me.skykistler.smudgr.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.midi.MidiMessage;

import me.skykistler.smudgr.Smudge;
import me.skykistler.smudgr.alg.Algorithm;
import me.skykistler.smudgr.alg.param.Parameter;
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
	private HashMap<Integer, ArrayList<Parameter>> midiMap;

	public Controller() {
		view = new View();
		midiMap = new HashMap<Integer, ArrayList<Parameter>>();
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
		for (Algorithm a : smudge.getAlgorithms()) {
			for (Parameter p : a.getParameters()) {
				if (p.isBindRequested()) {
					System.out.println("Binding " + p + " for " + p.getParent() + "... please touch a MIDI key...");
					bindParameter(p);
				}
			}
		}
	}

	private void bindParameter(Parameter p) {
		lastKeyPressed = -1;
		waitingForKey = true;

		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		ArrayList<Parameter> parameters = getBound(lastKeyPressed);
		if (parameters == null) {
			midiMap.put(lastKeyPressed, new ArrayList<Parameter>());
			getBound(lastKeyPressed).add(p);
			waitingForKey = false;
		} else {
			bindParameter(p);
		}
	}

	public ArrayList<Parameter> getBound(int key) {
		return midiMap.get(key);
	}

	private boolean waitingForKey = false;
	private int lastKeyPressed = -1;

	public void midiInput(MidiMessage message) {
		byte[] digest = message.getMessage();

		if (!waitingForKey)
			if (message.getLength() == 3) {
				int key = digest[1];
				int value = digest[2];

				ArrayList<Parameter> bound = getBound(key);
				if (bound != null)
					for (Parameter p : bound) {
						p.midiSet(value);
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
