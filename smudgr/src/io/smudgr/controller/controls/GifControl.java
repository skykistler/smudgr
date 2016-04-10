package io.smudgr.controller.controls;

import io.smudgr.controller.Controller;

public class GifControl extends Controllable {

	public String getName() {
		return "Record GIF";
	}

	private String filename;

	public GifControl() {
		requestBind();
	}

	public GifControl(String filename) {
		this();
		this.filename = filename;
	}

	public void inputValue(int value) {
	}

	public void inputOn(int value) {
		Controller.getInstance().startGifOutput(filename);
	}

	public void inputOff(int value) {
		Controller.getInstance().stopGifOutput();
	}

	public void increment() {
	}

	public void decrement() {
	}

	public void savePropertyMap() {
		getPropertyMap().setProperty("filename", filename);
	}

	public void loadPropertyMap() {
		filename = getPropertyMap().getProperty("filename");
	}

}
