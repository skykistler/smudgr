package io.smudgr.controller.controls;

import io.smudgr.controller.Controller;
import io.smudgr.source.Source;
import io.smudgr.source.SourceSet;

public class SourceControl extends Controllable {

	public SourceControl(Controller controller) {
		super(controller, "Source Switcher");

		requestBind();
	}

	public void inputValue(int value) {

	}

	public void inputOn(int value) {

	}

	public void inputOff(int value) {

	}

	public void increment() {
		Source s = getController().getSmudge().getSource();
		if (s instanceof SourceSet) {
			((SourceSet) s).increment();
		}
	}

	public void decrement() {
		Source s = getController().getSmudge().getSource();
		if (s instanceof SourceSet) {
			((SourceSet) s).decrement();
		}
	}

}
