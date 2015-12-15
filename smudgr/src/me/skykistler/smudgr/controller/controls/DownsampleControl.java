package me.skykistler.smudgr.controller.controls;

import me.skykistler.smudgr.Smudge;

public class DownsampleControl extends Controllable {

	private Smudge smudge;
	private int downsample = 1;

	public DownsampleControl(Smudge s) {
		super("Downsampler");
		smudge = s;
	}

	public void increment() {
		downsample++;
		smudge.downsample(downsample);
	}

	public void decrement() {
		downsample--;
		if (downsample < 1)
			downsample = 1;

		smudge.downsample(downsample);
	}

	public void midiValue(int value) {

	}

	public void noteOn(int note) {

	}

	public void noteOff(int note) {

	}

}
