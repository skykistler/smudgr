package io.smudgr.controller.controls;

import io.smudgr.controller.SmudgeController;

public class DownsampleControl extends Controllable {

	private int downsample;

	public DownsampleControl(SmudgeController c) {
		this(c, 1);
	}

	public DownsampleControl(SmudgeController c, int initial) {
		super(c, "Downsampler");
		downsample = initial;

		requestBind();
	}

	public void init() {
		getController().getSmudge().setDownsample(downsample);
	}

	public void increment() {
		downsample++;

		getController().getSmudge().setDownsample(downsample);
	}

	public void decrement() {
		downsample--;
		if (downsample < 1)
			downsample = 1;

		getController().getSmudge().setDownsample(downsample);
	}

	public void inputValue(int value) {

	}

	public void inputOn(int value) {

	}

	public void inputOff(int value) {

	}

}
