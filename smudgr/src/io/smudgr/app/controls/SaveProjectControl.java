package io.smudgr.app.controls;

import io.smudgr.project.ProjectSaver;

public class SaveProjectControl implements AppControl {

	public String getName() {
		return "Save Project";
	}

	public void init() {
	}

	public void update() {
	}

	public void inputValue(int value) {
	}

	public void inputOn(int value) {
		ProjectSaver xml = new ProjectSaver();
		xml.save();
	}

	public void inputOff(int value) {
	}

	public void increment() {
	}

	public void decrement() {
	}

}
