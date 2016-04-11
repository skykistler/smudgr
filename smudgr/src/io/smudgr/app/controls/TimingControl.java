package io.smudgr.app.controls;

import io.smudgr.app.Controller;

public class TimingControl extends Controllable {

	public String getName() {
		return "Timing Control";
	}

	public void inputValue(int value) {
		if (value > 0 && value <= 300)
			Controller.getInstance().setBPM(value);
	}

	public void inputOn(int value) {
		Controller.getInstance().start();
	}

	public void inputOff(int value) {
		if (value == 0)
			Controller.getInstance().pause();

		if (value == 1)
			Controller.getInstance().stop();
	}

	public void increment() {
	}

	public void decrement() {
	}

}
