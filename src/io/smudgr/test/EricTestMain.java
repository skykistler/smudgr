package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.Controller;
import io.smudgr.project.smudge.Smudge;
import io.smudgr.project.smudge.alg.Algorithm;
import io.smudgr.project.smudge.alg.op.SourceMixer;
import io.smudgr.project.smudge.alg.select.RangeSelect;

public class EricTestMain extends AppStart {

	/*
	 * To make your own test class, just duplicate this file and change the
	 * following
	 */

	// where to save/load project file
	static String projectPath = "data/test.smudge";

	// whether to overwrite the existing project file
	static boolean overwriteSmudge = true;

	// where to load source files from
	static String sourcePath = "data/work";

	// where to output gifs and stuff
	static String outputPath = "data/output";

	// name of device to bind to
	static String device = "Arturia BeatStep";

	// whether to start a server to broadcast midi signals
	static boolean deviceServer = false;

	// set to -1 for no fullscreen, set to 0 for fullscreen on main display
	// Higher numbers are for multi-monitor setups
	static int fullscreenDisplay = -1;

	// non-fullscreen window
	static boolean monitor = true;

	public void buildSmudge() {
		// Pro-tip: In eclipse, you can Ctrl+Click on a class name to quickly
		// open that class
		// There, you can see the names of available parameters
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		// Put algorithm/smudge building stuff here
		Algorithm alg = new Algorithm();

		// Example Selector
		RangeSelect range = new RangeSelect();
		// alg.add(range);

		SourceMixer mixer = new SourceMixer();
		alg.add(mixer);

		// Make sure to add any new algorithms to the smudge
		smudge.add(alg);

		bind(mixer.getParameter("Size"));
		bind(mixer.getParameter("Translation X"));
		bind(mixer.getParameter("Translation Y"));
		bind(mixer.getParameter("Blender"));

		// This is how you make an automated thingy, I've included a method
		// writing this easier
		bind(smudge.getParameter("Downsample"));

		bind(Controller.getInstance().getAppControl("Source Set Switcher"));
		bind(Controller.getInstance().getAppControl("Source Switcher"));
		bind(Controller.getInstance().getAppControl("Record GIF"));
		bind(Controller.getInstance().getAppControl("Save Project"));
		bind(Controller.getInstance().getAppControl("Save Frame"));
	}

	public EricTestMain() {
		super(projectPath, sourcePath, outputPath, device, overwriteSmudge, deviceServer);

		fullscreenView(fullscreenDisplay);
		if (monitor)
			monitorView();

		start();
	}

	public static void main(String[] args) {
		new EricTestMain();
	}

}
