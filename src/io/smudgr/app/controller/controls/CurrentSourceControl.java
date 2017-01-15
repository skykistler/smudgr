package io.smudgr.app.controller.controls;

import io.smudgr.app.controller.Controller;
import io.smudgr.smudge.source.Source;
import io.smudgr.smudge.source.SourceSet;

public class CurrentSourceControl implements AppControl {

	public String getName() {
		return "Source Switcher";
	}

	public void inputValue(int value) {
	}

	public void inputOn() {
	}

	public void inputOff() {
	}

	public void increment() {
		Source src = Controller.getInstance().getProject().getSmudge().getSource();

		if (src instanceof SourceSet)
			((SourceSet) src).nextSource();
	}

	public void decrement() {
		Source src = Controller.getInstance().getProject().getSmudge().getSource();

		if (src instanceof SourceSet)
			((SourceSet) src).previousSource();
	}

}
