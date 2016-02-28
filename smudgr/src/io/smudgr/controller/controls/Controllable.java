package io.smudgr.controller.controls;

import io.smudgr.controller.Controller;

public abstract class Controllable {

	private Controller controller;
	private String name;
	private boolean bindRequested = false;

	public Controllable(Controller controller, String name) {
		this(name);

		this.controller = controller;
		controller.addControl(this);
	}

	public Controllable(String name) {
		this.name = name;
	}

	public Controller getController() {
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
