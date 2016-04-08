package io.smudgr.controller.controls;

import io.smudgr.smudge.source.Source;
import io.smudgr.smudge.source.SourceSet;

public class SourceControl extends Controllable {

	public SourceControl() {
		super("Source Switcher");

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
