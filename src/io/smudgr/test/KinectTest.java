package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.controller.Controller;
import io.smudgr.app.view.PiFullscreenView;
import io.smudgr.engine.Smudge;
import io.smudgr.engine.alg.Algorithm;
import io.smudgr.engine.alg.op.DataBend;
import io.smudgr.engine.alg.op.SpectralShift;
import io.smudgr.extensions.automate.controls.AutomatorControl;
import io.smudgr.extensions.kinect.source.VideoSource;

/**
 * Used for testing the kinect
 */
public class KinectTest extends AppStart {

	/*
	 * To make your own test class, just duplicate this file and change the
	 * following
	 */

	// where to save/load project file
	static String projectPath = "data/test.smudge";

	// whether to load the existing file or delete it and make a new one
	static boolean newSmudge = true;

	// where to load source files from
	static String sourcePath = "data/test";

	// where to output gifs and stuff
	static String outputPath = "data";

	// name of device to bind to
	static String device = isMac() ? "PAD" : "nanoPAD2";

	// whether to start a server to broadcast midi signals
	static boolean deviceServer = true;

	// set to -1 for no fullscreen, set to 0 for fullscreen on main display
	// Higher numbers are for multi-monitor setups
	static int fullscreenDisplay = 0;

	// non-fullscreen window
	static boolean monitor = true;

	/**
	 * Create
	 */
	public KinectTest() {
		super(projectPath, sourcePath, outputPath, device, newSmudge, deviceServer);

		// fullscreenView(fullscreenDisplay);
		// if (monitor)
		// monitorView();

		Controller.getInstance().add(new PiFullscreenView(fullscreenDisplay));

		start();
	}

	@Override
	public void buildSmudge() {
		// Pro-tip: In eclipse, you can Ctrl+Click on a class name to quickly
		// open that class
		// There, you can see the names of available parameters
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		smudge.setSource(new VideoSource());

		// Put algorithm/smudge building stuff here
		Algorithm alg = new Algorithm();

		// Example Selector
		// RangeSelect range = new RangeSelect();
		// alg.add(range);

		// Example operation
		DataBend databend = new DataBend();

		SpectralShift sshift = new SpectralShift();
		alg.add(sshift);
		AutomatorControl automator2 = addAutomator("Animate", sshift.getParameter("Shift"));
		bind(automator2);
		sshift.getParameter("Colors").setValue(150);
		sshift.getParameter("Sort").setValue(true);
		// alg.add(databend);

		// Make sure to add any new algorithms to the smudge
		smudge.add(alg);

		databend.getParameter("Amount").setValue(2);
		// This is how you make an automated thingy, I've included a method
		// writing this easier
		AutomatorControl automator1 = addAutomator("Animate", databend.getParameter("Target"));

		bind(smudge.getParameter("Downsample"));
		// bind(databend.getParameter("Amount"));

		bind(automator1);
		// bind(range.getParameter("Range Length"));

		bind(Controller.getInstance().getAppControl("Source Set Switcher"));
		bind(Controller.getInstance().getAppControl("Source Switcher"));
		bind(Controller.getInstance().getAppControl("Record GIF"));
		bind(Controller.getInstance().getAppControl("Save Project"));

	}

	/**
	 * Run
	 *
	 * @param args
	 *            unused
	 */
	public static void main(String[] args) {
		new KinectTest();
	}
}
