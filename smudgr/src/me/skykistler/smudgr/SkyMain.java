package me.skykistler.smudgr;

import me.skykistler.smudgr.alg.CubicMarbeler;
import me.skykistler.smudgr.alg.LumaSort;
import me.skykistler.smudgr.controller.Controller;

public class SkyMain {

	public static void main(String[] args) {
		Smudge smudge = new Smudge("house1.png");

		CubicMarbeler marbeler = new CubicMarbeler();
		marbeler.bind("Offset - X/Y");
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
