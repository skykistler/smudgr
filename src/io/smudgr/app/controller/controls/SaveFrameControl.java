package io.smudgr.app.controller.controls;

import io.smudgr.app.controller.AppControl;
import io.smudgr.util.Frame;
import io.smudgr.util.output.ImageOutput;

/**
 * Capture and save the currently rendered frame.
 * 
 * @see ImageOutput
 */
public class SaveFrameControl implements AppControl {

	/**
	 * @return "Save Frame"
	 */
	@Override
	public String getName() {
		return "Save Frame";
	}

	/**
	 * Does nothing.
	 * 
	 * @see SaveFrameControl#inputOn()
	 */
	@Override
	public void inputValue(int value) {
	}

	/**
	 * Capture and save the currently rendered frame.
	 * 
	 * @see ImageOutput
	 */
	@Override
	public void inputOn() {
		Frame frame = getProject().getSmudge().getFrame();

		// TODO project wide output folder
		ImageOutput out = new ImageOutput("frame", frame.getWidth(), frame.getHeight());
		out.addFrame(frame);
	}

	/**
	 * Does nothing.
	 * 
	 * @see SaveFrameControl#inputOn()
	 */
	@Override
	public void inputOff() {
	}

	/**
	 * Does nothing.
	 * 
	 * @see SaveFrameControl#inputOn()
	 */
	@Override
	public void increment() {
	}

	/**
	 * Does nothing.
	 * 
	 * @see SaveFrameControl#inputOn()
	 */
	@Override
	public void decrement() {
	}

}
