package me.skykistler.smudgr;

import me.skykistler.smudgr.alg.LumaSort;
import me.skykistler.smudgr.controller.Controller;

public class EricMain {
	public static void main(String[] args) {
		// Make a new smudge pointing at a file name
		Smudge smudge = new Smudge("house1.png");
		// optionally downsample
		// smudge.downsample(2);

		// Make a new algorithm instance and list it's parameters
		LumaSort lumaSort = new LumaSort();
		lumaSort.listParameters();

		// Set some values
		lumaSort.getParameter("Luma Threshold X").setValue(20.0);
		lumaSort.getParameter("Sort Columns").setValue(false);
		lumaSort.getParameter("Starting Row Bound").setValue(300);
		lumaSort.getParameter("Ending Row Bound").setValue(1000);
		lumaSort.getParameter("Starting Column Bound").setValue(300);
		lumaSort.getParameter("Ending Column Bound").setValue(400);
		smudge.addAlgorithm(lumaSort);

		// Start smudge using the controller
		Controller.startSmudge(smudge);

		// Save the first frame
		// smudge.save();
	}
}
