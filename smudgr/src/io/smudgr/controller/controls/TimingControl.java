package io.smudgr.controller.controls;

import io.smudgr.controller.BaseController;

public class TimingControl extends Controllable {

	public String getName() {
		return "Timing Control";
	}

	public void inputValue(int value) {
		if (value > 0 && value <= 300)
			BaseController.getInstance().setBPM(value);
	}

	public void inputOn(int value) {
		BaseController.getInstance().start();
	}

	public void inputOff(int value) {
		if (value == 0)
			BaseController.getInstance().pause();

		if (value == 1)
			BaseController.getInstance().stop();
	}

	public void increment() {
	}

	public void decrement() {
	}

}
