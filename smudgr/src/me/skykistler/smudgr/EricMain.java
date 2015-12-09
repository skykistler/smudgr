package me.skykistler.smudgr;

import me.skykistler.smudgr.alg.LumaSort;
import me.skykistler.smudgr.alg.param.DoubleParameter;
import me.skykistler.smudgr.controller.Controller;

public class EricMain {
	public static void main(String[] args) {
		// Make a new smudge pointing at a file name
		Smudge smudge = new Smudge("filename.png");
		// optionally downsample
		// smudge.downsample(2);

		// Make a new algorithm instance and list it's parameters
		LumaSort lumaSort = new LumaSort();
		lumaSort.listParameters();

		// Set some values, this is a little messy right now
		((DoubleParameter) lumaSort.getParameter("Luma Threshold X")).setValue(40);

		smudge.addAlgorithm(lumaSort);

		// Start smudge using the controller
		Controller.startSmudge(smudge);

		// Save the first frame
		// smudge.save();
	}
}
