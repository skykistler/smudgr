package io.smudgr.controller.controls;

import io.smudgr.controller.BaseController;

public class GifControl extends Controllable {

	private String filename;

	public GifControl() {
		super("Record GIF");
	}

	public GifControl(String filename) {
		this();

		this.filename = filename;
		requestBind();
	}

	public void inputValue(int value) {
	}

	public void inputOn(int value) {
		BaseController.getInstance().startGifOutput(filename);
	}

	public void inputOff(int value) {
		BaseController.getInstance().stopGifOutput();
	}

	public void increment() {
	}

	public void decrement() {
	}

	public void setProperties() {
		getPropertyMap().setProperty("filename", filename);
	}

	public void getProperties() {
		filename = getPropertyMap().getProperty("filename");
	}

}
