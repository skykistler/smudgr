package io.smudgr.app.controls;

import io.smudgr.app.Controller;
import io.smudgr.app.output.GifOutput;

public class RecordGIFControl implements AppControl {

	public String getName() {
		return "Record GIF";
	}

	public void init() {
	}

	public void update() {
	}

	public void inputValue(int value) {
	}

	public void inputOn(int value) {
		// TODO project wide output folder
		Controller.getInstance().startOutput(new GifOutput("record"));
	}

	public void inputOff(int value) {
		Controller.getInstance().stopOutput();
	}

	public void increment() {
	}

	public void decrement() {
	}

}
