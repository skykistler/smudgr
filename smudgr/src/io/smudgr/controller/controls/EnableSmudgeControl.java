package io.smudgr.controller.controls;

import io.smudgr.Smudge;
import io.smudgr.controller.Controller;

public class EnableSmudgeControl extends Controllable {

	public EnableSmudgeControl(Controller controller) {
		super(controller, "Enable Smudge");

		requestBind();
	}

	public void inputValue(int value) {
		if (value == 0)
			getController().getSmudge().setEnabled(false);
		else
			getController().getSmudge().setEnabled(true);
	}

	public void inputOn(int value) {
		getController().getSmudge().setEnabled(true);
	}

	public void inputOff(int value) {
		getController().getSmudge().setEnabled(false);
	}

	public void increment() {
		Smudge s = getController().getSmudge();
		s.setEnabled(!s.isEnabled());
	}

	public void decrement() {
		increment();
	}

}
