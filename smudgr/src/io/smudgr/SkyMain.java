package io.smudgr;

import io.smudgr.alg.PixelSort;
import io.smudgr.controller.Controller;

public class SkyMain {

	public static void main(String[] args) {
		Smudge smudge = new Smudge("test", "galaxies.jpg");
		smudge.downsample(2);

		//		SaveControl save = new SaveControl(smudge);
		//		save.requestBind();
		//
		//		ImageSwitcherControl imageSwitcher = new ImageSwitcherControl(smudge, "city");
		//		imageSwitcher.requestBind();
		//
		//		DownsampleControl downsampler = new DownsampleControl(smudge);
		//		downsampler.requestBind();

		PixelSort pixelSort = new PixelSort();

		smudge.addAlgorithm(pixelSort);

		//		Controller.startSmudge(smudge, "Arturia Beatstep Pro");
		Controller.startSmudge(smudge);
	}

}
