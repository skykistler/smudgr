package me.skykistler.smudgr.controller.controls;

import java.util.ArrayList;

public abstract class Controllable {
	private static ArrayList<Controllable> controls = new ArrayList<Controllable>();

	public static ArrayList<Controllable> getControls() {
		return controls;
	}

	private String name;
	private boolean bindRequested = false;

	public Controllable(String name) {
		this.name = name;

		controls.add(this);
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return getName();
	}

	public void init() {

	}

	public abstract void midiValue(int value);

	public abstract void noteOn(int note);

	public abstract void noteOff(int note);

	public abstract void increment();

	public abstract void decrement();

	public void requestBind() {
		bindRequested = true;
	}

	public boolean isBindRequested() {
		return bindRequested;
	}
}
