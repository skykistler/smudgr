package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.controller.Controller;
import io.smudgr.engine.Smudge;
import io.smudgr.engine.alg.Algorithm;
import io.smudgr.engine.alg.coord.RowCoords;
import io.smudgr.engine.alg.op.PixelShift;

public class PixelShiftTest extends AppStart {

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
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		Algorithm alg = new Algorithm();

		PixelShift shift = new PixelShift();
		alg.add(shift);
		alg.add(new RowCoords());
		smudge.add(alg);

		shift.getParameter("Intervals").setContinuous(true);
		shift.getParameter("Shift Type").setValue(3);

		shift.getParameter("Intervals").setValue(20);
		addAutomator("Animate", shift.getParameter("Amount"));
		// addAutomator("Animate", shift.getParameter("Intervals"));

		smudge.getParameter("Downsample").setValue(1);

	}

	public PixelShiftTest() {
		super(projectPath, sourcePath, outputPath, device, overwriteSmudge, deviceServer);

		fullscreenView(fullscreenDisplay);
		if (monitor)
			monitorView();

		start();
	}

	public static void main(String[] args) {
		new PixelShiftTest();
	}

}
