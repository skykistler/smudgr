package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.Controller;
import io.smudgr.extensions.automate.controls.AutomatorControl;
import io.smudgr.project.smudge.Smudge;
import io.smudgr.project.smudge.alg.Algorithm;
import io.smudgr.project.smudge.alg.op.DataBend;
import io.smudgr.project.smudge.alg.select.RangeSelect;

public class SkyTestApp extends AppStart {

	/*
	 * To make your own test class, just duplicate this file and change the following
	 */

	// where to save/load project file
	static String projectPath = "data/test.smudge";

	// whether to load the existing file or delete it and make a new one
	static boolean newSmudge = true;

	// where to load source files from
	static String sourcePath = "data/venture/oceans";

	// where to output gifs and stuff
	static String outputPath = "data";

	// name of device to bind to
	static String device = "Arturia BeatStepPro";

	// set to -1 for no fullscreen, set to 0 for fullscreen on main display
	// Higher numbers are for multi-monitor setups
	static int fullscreenDisplay = -1;

	// non-fullscreen window
	static boolean monitor = true;

	public void buildSmudge() {
		// Pro-tip: In eclipse, you can Ctrl+Click on a class name to quickly open that class
		// There, you can see the names of available parameters
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		// Put algorithm/smudge building stuff here
		Algorithm alg = new Algorithm();

		// Example Selector
		RangeSelect range = new RangeSelect();
		alg.add(range);

		// Example operation
		DataBend databend = new DataBend();
		alg.add(databend);

		// Make sure to add any new algorithms to the smudge
		smudge.add(alg);

		// This is how you make an automated thingy, I've included a method writing this easier
		AutomatorControl automator1 = addAutomator("Animate", databend.getParameter("Target"));

		bind(smudge.getParameter("Downsample"));
		bind(databend.getParameter("Amount"));
		bind(automator1);
		bind(range.getParameter("Range Length"));

		bind(Controller.getInstance().getAppControl("Source Set Switcher"));
		bind(Controller.getInstance().getAppControl("Source Switcher"));
		bind(Controller.getInstance().getAppControl("Record GIF"));
		bind(Controller.getInstance().getAppControl("Save Project"));
	}

	public SkyTestApp() {
		super(projectPath, sourcePath, outputPath, device);

		fullscreenView(fullscreenDisplay);
		if (monitor)
			monitorView();

		start(newSmudge);
	}

	public static void main(String[] args) {
		new SkyTestApp();
	}

}
