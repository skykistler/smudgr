package io.smudgr.app.controller.controls;

import io.smudgr.util.Frame;
import io.smudgr.util.output.ImageOutput;

public class SaveFrameControl implements AppControl {

	public String getName() {
		return "Save Frame";
	}

	public void inputValue(int value) {
	}

	public void inputOn() {
		Frame frame = getProject().getSmudge().getFrame();

		// TODO project wide output folder
		ImageOutput out = new ImageOutput("frame", frame.getWidth(), frame.getHeight());
		out.addFrame(frame);
	}

	public void inputOff() {
	}

	public void increment() {
	}

	public void decrement() {
	}

}
