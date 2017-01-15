package io.smudgr.app.controller.controls;

import io.smudgr.app.controller.Controller;

public class SourceLibraryControl implements AppControl {

	public String getName() {
		return "Source Set Switcher";
	}

	public void inputValue(int value) {
	}

	public void inputOn() {
	}

	public void inputOff() {
	}

	public void increment() {
		Controller.getInstance().getProject().getSourceLibrary().nextSet();
	}

	public void decrement() {
		Controller.getInstance().getProject().getSourceLibrary().previousSet();
	}

}
