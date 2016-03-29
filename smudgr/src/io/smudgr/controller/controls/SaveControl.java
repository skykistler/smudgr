package io.smudgr.controller.controls;

import io.smudgr.output.ProjectXML;

public class SaveControl extends Controllable {

	private String filepath;

	public SaveControl() {
		super("Save Project");
	}

	public SaveControl(String filepath) {
		this();

		this.filepath = filepath;

		requestBind();
	}

	public void inputValue(int value) {
	}

	public void inputOn(int value) {
		ProjectXML xml = new ProjectXML(filepath);
		xml.save(getController());
	}

	public void inputOff(int value) {
	}

	public void increment() {
	}

	public void decrement() {
	}

	public void setProperties() {
		getPropertyMap().setProperty("filepath", filepath);
	}

	public void getProperties() {
		filepath = getPropertyMap().getProperty("filepath");
	}

}
