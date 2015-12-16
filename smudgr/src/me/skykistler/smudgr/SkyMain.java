package me.skykistler.smudgr;

import me.skykistler.smudgr.alg.LumaSort;
import me.skykistler.smudgr.controller.Controller;
import me.skykistler.smudgr.controller.controls.DownsampleControl;
import me.skykistler.smudgr.controller.controls.ImageSwitcherControl;
import me.skykistler.smudgr.controller.controls.SaveControl;

public class SkyMain {

	public static void main(String[] args) {
		Smudge smudge = new Smudge("city test", "city/manhole.png");
		smudge.downsample(2);

		SaveControl save = new SaveControl(smudge);
		save.requestBind();

		ImageSwitcherControl imageSwitcher = new ImageSwitcherControl(smudge, "city");
		imageSwitcher.requestBind();

		DownsampleControl downsampler = new DownsampleControl(smudge);
		downsampler.requestBind();

		LumaSort lumaSort = new LumaSort();
		lumaSort.getParameter("Luma Threshold X").setReverse(true);
		lumaSort.getParameter("Luma Threshold Y").setReverse(true);

		lumaSort.bind("Luma Threshold X");
		lumaSort.bind("Luma Threshold Y");
		lumaSort.bind("Starting Row Bound");
		lumaSort.bind("Starting Column Bound");
		lumaSort.bind("Ending Column Bound");
		lumaSort.bind("Ending Row Bound");

		smudge.addAlgorithm(lumaSort);

		Controller.startSmudge(smudge, "Arturia BeatStep");
	}

}
