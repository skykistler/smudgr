package io.smudgr.controller.controls;

import io.smudgr.controller.Controller;

public class DownsampleControl extends Controllable {

	private int downsample;

	public DownsampleControl(Controller c) {
		this(c, 1);
	}

	public DownsampleControl(Controller c, int initial) {
		super(c, "Downsampler");
		downsample = initial;

		requestBind();
	}

	public void init() {
		getController().getSmudge().downsample(downsample);
	}

	public void increment() {
		downsample++;

		getController().getSmudge().downsample(downsample);
	}

	public void decrement() {
		downsample--;
		if (downsample < 1)
			downsample = 1;

		getController().getSmudge().downsample(downsample);
	}

	public void inputValue(int value) {

	}

	public void inputOn(int value) {

	}

	public void inputOff(int value) {

	}

}
