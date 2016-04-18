package io.smudgr.app.controls;

import io.smudgr.app.Controller;

public class SourceLibraryControl implements AppControl {

	public String getName() {
		return "Source Set Switcher";
	}

	public void inputValue(int value) {
	}

	public void inputOn(int value) {
	}

	public void inputOff(int value) {
	}

	public void increment() {
		Controller.getInstance().getProject().getSourceLibrary().nextSet();
	}

	public void decrement() {
		Controller.getInstance().getProject().getSourceLibrary().previousSet();
	}

}
