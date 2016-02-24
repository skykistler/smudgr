package io.smudgr.controller.controls;

import io.smudgr.controller.SmudgeController;

public abstract class Controllable {
	private SmudgeController controller;
	private String name;
	private boolean bindRequested = false;

	public Controllable(SmudgeController controller, String name) {
		this.controller = controller;
		this.name = name;

		controller.addControl(this);
	}

	public SmudgeController getController() {
		return controller;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return getName();
	}

	public void init() {

	}

	public void update() {

	}

	public abstract void inputValue(int value);

	public abstract void inputOn(int value);

	public abstract void inputOff(int value);

	public abstract void increment();

	public abstract void decrement();

	public void requestBind() {
		bindRequested = true;
	}

	public boolean isBindRequested() {
		return bindRequested;
	}
}
