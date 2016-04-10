package io.smudgr.controller.controls;

import io.smudgr.controller.BaseController;

public class DownsampleControl extends Controllable {

	public String getName() {
		return "Downsampler";
	}

	private int downsample;

	public DownsampleControl(int initial) {
		downsample = initial;

		requestBind();
	}

	public void update() {
		BaseController.getInstance().getSmudge().setDownsample(downsample);
	}

	public void increment() {
		downsample++;
	}

	public void decrement() {
		downsample--;
		if (downsample < 1)
			downsample = 1;
	}

	public void inputValue(int value) {

	}

	public void inputOn(int value) {

	}

	public void inputOff(int value) {

	}

	public void savePropertyMap() {
		getPropertyMap().setProperty("downsample", downsample);
	}

	public void loadPropertyMap() {
		downsample = Integer.parseInt(getPropertyMap().getProperty("downsample"));
	}

}
