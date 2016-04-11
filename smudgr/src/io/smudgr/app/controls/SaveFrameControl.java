package io.smudgr.app.controls;

import io.smudgr.app.Controller;
import io.smudgr.record.ImageOutput;
import io.smudgr.smudge.source.Frame;

public class SaveFrameControl extends Controllable {

	public String getName() {
		return "Save Frame";
	}

	private String filename;

	public SaveFrameControl() {
		requestBind();
	}

	public SaveFrameControl(String filename) {
		this();
		this.filename = filename;
	}

	public void inputValue(int value) {
	}

	public void inputOn(int value) {
		Frame frame = Controller.getInstance().getSmudge().getFrame();

		ImageOutput out = new ImageOutput(filename, frame.getWidth(), frame.getHeight());
		out.addFrame(frame);
	}

	public void inputOff(int value) {
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
