package io.smudgr.app.controller.controls;

import io.smudgr.app.controller.AppControl;
import io.smudgr.app.controller.Controller;
import io.smudgr.util.output.GifOutput;

/**
 * Control for starting and stopping recording to a GIF image.
 * 
 * @see GifOutput
 */
public class RecordGIFControl implements AppControl {

	/**
	 * @return "Record GIF"
	 */
	@Override
	public String getElementName() {
		return "Record GIF";
	}

	/**
	 * Does nothing.
	 * 
	 * @see RecordGIFControl#inputOn()
	 * @see RecordGIFControl#inputOff()
	 */
	@Override
	public void inputValue(int value) {
	}

	/**
	 * Start recording a GIF.
	 * 
	 * @see RecordGIFControl#inputOff()
	 */
	@Override
	public void inputOn() {
		// TODO project wide output folder
		Controller.getInstance().startOutput(new GifOutput("record"));
	}

	/**
	 * Stop recording a GIF.
	 * 
	 * @see RecordGIFControl#inputOn()
	 */
	@Override
	public void inputOff() {
		Controller.getInstance().stopOutput();
	}

	/**
	 * Does nothing.
	 * 
	 * @see RecordGIFControl#inputOn()
	 * @see RecordGIFControl#inputOff()
	 */
	@Override
	public void increment() {
	}

	/**
	 * Does nothing.
	 * 
	 * @see RecordGIFControl#inputOn()
	 * @see RecordGIFControl#inputOff()
	 */
	@Override
	public void decrement() {
	}

}
