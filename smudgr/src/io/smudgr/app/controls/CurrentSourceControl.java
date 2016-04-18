package io.smudgr.app.controls;

import io.smudgr.app.Controller;
import io.smudgr.project.smudge.source.Source;
import io.smudgr.project.smudge.source.SourceSet;

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
