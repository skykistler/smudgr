package me.skykistler.smudgr;

import me.skykistler.smudgr.alg.CubicMarbeler;
import me.skykistler.smudgr.alg.LumaSort;
import me.skykistler.smudgr.controller.Controller;
import me.skykistler.smudgr.controller.controls.DownsampleControl;
import me.skykistler.smudgr.controller.controls.ImageSwitcherControl;
import me.skykistler.smudgr.controller.controls.SaveControl;

public class SkyMain {

	public static void main(String[] args) {
		Smudge smudge = new Smudge("Winter is Coming", "winter is coming/1.jpg");
		smudge.downsample(2);

		SaveControl save = new SaveControl(smudge);
		save.requestBind();

		ImageSwitcherControl imageSwitcher = new ImageSwitcherControl(smudge, "winter is coming");
		imageSwitcher.requestBind();

		DownsampleControl downsampler = new DownsampleControl(smudge);
		downsampler.requestBind();

		CubicMarbeler marbeler = new CubicMarbeler();
		marbeler.bind("Offset - X/Y");
		marbeler.getParameter("Offset - X/Y").setContinuous(true);
		marbeler.bind("Seed");
		marbeler.bind("Frequency");
		marbeler.bind("Iterations");
		marbeler.bind("Strength");

		LumaSort lumaSort = new LumaSort();
		lumaSort.bind("Luma Threshold X");
		lumaSort.getParameter("Luma Threshold X").setReverse(true);
		lumaSort.bind("Luma Threshold Y");
		lumaSort.getParameter("Luma Threshold Y").setReverse(true);

		smudge.addAlgorithm(marbeler);
		smudge.addAlgorithm(lumaSort);

		Controller.startSmudge(smudge, "Arturia BeatStep");
	}

}
