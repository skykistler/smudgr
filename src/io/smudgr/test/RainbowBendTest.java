package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.controller.Controller;
import io.smudgr.engine.Smudge;
import io.smudgr.engine.alg.Algorithm;
import io.smudgr.engine.alg.op.RainbowBend;

/**
 * Used for testing some Eric stuff
 */
public class RainbowBendTest extends AppStart {

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

	@Override
	public void buildSmudge() {
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		Algorithm alg = new Algorithm();

		RainbowBend bend = new RainbowBend();
		alg.add(bend);
		smudge.add(alg);

		bend.getParameter("Amount").setValue(3);
		addAutomator("Animate", bend.getParameter("Target"));

		smudge.getParameter("Downsample").setValue(1);

	}

	/**
	 * Create
	 */
	public RainbowBendTest() {
		super(projectPath, sourcePath, outputPath, device, overwriteSmudge, deviceServer);

		fullscreenView(fullscreenDisplay);
		if (monitor)
			monitorView();

		start();
	}

	/**
	 * Run
	 * 
	 * @param args
	 *            unused
	 */
	public static void main(String[] args) {
		new RainbowBendTest();
	}

}
