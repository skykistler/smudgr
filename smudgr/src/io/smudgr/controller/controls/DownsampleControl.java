package io.smudgr.controller.controls;

public class DownsampleControl extends Controllable {

	private int downsample;

	public DownsampleControl() {
		super("Downsampler");
	}

	public DownsampleControl(int initial) {
		this();
		downsample = initial;

		requestBind();
	}

	public void update() {
		getController().getSmudge().setDownsample(downsample);
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

	public void saveProperties() {
		getPropertyMap().setProperty("downsample", downsample);
	}

	public void loadProperties() {
		downsample = Integer.parseInt(getPropertyMap().getProperty("downsample"));
	}

}
