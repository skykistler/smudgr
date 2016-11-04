package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.Controller;
import io.smudgr.project.smudge.Smudge;
import io.smudgr.project.smudge.alg.Algorithm;
import io.smudgr.project.smudge.alg.op.ChannelBleed;

public class ChannelBleedTest extends AppStart {

	/*
	 * To make your own test class, just duplicate this file and change the
	 * following
	 */

	// where to save/load project file
	static String projectPath = "data/test.smudge";

	// whether to overwrite the existing project file
	static boolean overwriteSmudge = true;

	// where to load source files from
	static String sourcePath = "data";

	// where to output gifs and stuff
	static String outputPath = "data";

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
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		Algorithm alg = new Algorithm();

		ChannelBleed bleed = new ChannelBleed();
		alg.add(bleed);
		smudge.add(alg);

		// bleed.getParameter("Amount").setValue(3);
		addAutomator("Animate", bleed.getParameter("Shift Amount"));

		bleed.getParameter("Shift Amount").setContinuous(true);

		smudge.getParameter("Downsample").setValue(1);

	}

	public ChannelBleedTest() {
		super(projectPath, sourcePath, outputPath, device, overwriteSmudge, deviceServer);

		fullscreenView(fullscreenDisplay);
		if (monitor)
			monitorView();

		start();
	}

	public static void main(String[] args) {
		new ChannelBleedTest();
	}

}
