package io.smudgr.app.controls;

import io.smudgr.project.util.ProjectSaver;

public class SaveProjectControl implements AppControl {

	public String getName() {
		return "Save Project";
	}

	public void inputValue(int value) {
	}

	public void inputOn() {
		ProjectSaver xml = new ProjectSaver();
		xml.save();
	}

	public void inputOff() {
	}

	public void increment() {
	}

	public void decrement() {
	}

}
