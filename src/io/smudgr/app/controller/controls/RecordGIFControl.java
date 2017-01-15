package io.smudgr.app.controller.controls;

import io.smudgr.app.controller.Controller;
import io.smudgr.util.output.GifOutput;

public class RecordGIFControl implements AppControl {

	public String getName() {
		return "Record GIF";
	}

	public void inputValue(int value) {
	}

	public void inputOn() {
		// TODO project wide output folder
		Controller.getInstance().startOutput(new GifOutput("record"));
	}

	public void inputOff() {
		Controller.getInstance().stopOutput();
	}

	public void increment() {
	}

	public void decrement() {
	}

}