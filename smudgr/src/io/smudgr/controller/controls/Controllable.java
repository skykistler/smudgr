package io.smudgr.controller.controls;

import io.smudgr.controller.Controller;

public abstract class Controllable {
	private String name;
	private boolean bindRequested = false;

	public Controllable(String name) {
		this.name = name;
		Controller.getInstance().addControl(this);
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
