package io.smudgr.app.controls;

import io.smudgr.project.ProjectXML;

public class SaveProjectControl extends Controllable {

	public String getName() {
		return "Save Project";
	}

	private String filepath;

	public SaveProjectControl() {
		requestBind();
	}

	public SaveProjectControl(String filepath) {
		this();
		this.filepath = filepath;
	}

	public void inputValue(int value) {
	}

	public void inputOn(int value) {
		ProjectXML xml = new ProjectXML(filepath);
		xml.save();
	}

	public void inputOff(int value) {
	}

	public void increment() {
	}

	public void decrement() {
	}

	public void savePropertyMap() {
		getPropertyMap().setProperty("filepath", filepath);
	}

	public void loadPropertyMap() {
		filepath = getPropertyMap().getProperty("filepath");
	}

}
