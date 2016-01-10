package io.smudgr.controller.controls;

import io.smudgr.controller.Controller;

public class SaveControl extends Controllable {

	public SaveControl(Controller c) {
		super(c, "Save Frame");

		requestBind();
	}

	public void init() {

	}

	public void inputValue(int value) {
	}

	public void inputOn(int value) {
		getController().getSmudge().save();
	}

	public void inputOff(int value) {
	}

	public void increment() {
	}

	public void decrement() {
	}

}
