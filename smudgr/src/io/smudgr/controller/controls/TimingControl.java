package io.smudgr.controller.controls;

import io.smudgr.controller.BaseController;

public class TimingControl extends Controllable {

	public TimingControl() {
		super("Timing Control");
	}

	public void inputValue(int value) {
		if (value > 0 && value <= 300)
			((BaseController) getController()).setBPM(value);
	}

	public void inputOn(int value) {
		getController().start();
	}

	public void inputOff(int value) {
		if (value == 0)
			getController().pause();

		if (value == 1)
			getController().stop();
	}

	public void increment() {
	}

	public void decrement() {
	}

}
