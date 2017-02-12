package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.controller.Controller;
import io.smudgr.engine.Smudge;
import io.smudgr.engine.alg.Algorithm;
import io.smudgr.engine.alg.op.ChannelBleed;
import io.smudgr.engine.alg.op.ChannelCrush;
import io.smudgr.engine.alg.op.ChannelDrift;
import io.smudgr.engine.alg.op.ChannelSort;
import io.smudgr.engine.alg.op.DataBend;
import io.smudgr.engine.alg.op.HSVLModifier;
import io.smudgr.engine.alg.op.Marbeler;
import io.smudgr.engine.alg.op.Operation;
import io.smudgr.engine.alg.op.ParticlePush;
import io.smudgr.engine.alg.op.PixelShift;
import io.smudgr.engine.alg.op.PixelSort;
import io.smudgr.engine.alg.op.RainbowBend;
import io.smudgr.engine.alg.op.Smear;
import io.smudgr.engine.alg.op.SpectralShift;
import io.smudgr.extensions.cef.view.WebsocketView;

/**
 * Example class for creating a test {@link AppStart} main class
 */
public class SkyTestApp extends AppStart {

	/*
	 * To make your own test class, just duplicate this file and change the
	 * following
	 */

	// where to save/load project file
	static String projectPath = "data/test.smudge";

	// whether to overwrite the existing project file
	static boolean overwriteSmudge = true;

	// where to load source files from
	static String sourcePath = "data/testing";

	// where to output gifs and stuff
	static String outputPath = "data/output";

	// name of device to bind to
	static String device = "Arturia BeatStepPro";

	// whether to start a server to broadcast midi signals
	static boolean deviceServer = false;

	// set to -1 for no fullscreen, set to 0 for fullscreen on main display
	// Higher numbers are for multi-monitor setups
	static int fullscreenDisplay = -1;

	// non-fullscreen window
	static boolean monitor = false;

	// whether to allow front-end streaming connections
	static boolean streaming = true;

	@Override
	public void buildSmudge() {
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		getOperationAlgorithm(smudge, new ChannelBleed());
		getOperationAlgorithm(smudge, new ChannelCrush());
		getOperationAlgorithm(smudge, new ChannelDrift());
		getOperationAlgorithm(smudge, new ChannelSort());
		getOperationAlgorithm(smudge, new DataBend());
		getOperationAlgorithm(smudge, new HSVLModifier());
		getOperationAlgorithm(smudge, new Marbeler());
		getOperationAlgorithm(smudge, new ParticlePush());
		getOperationAlgorithm(smudge, new PixelShift());
		getOperationAlgorithm(smudge, new PixelSort());
		getOperationAlgorithm(smudge, new RainbowBend());
		getOperationAlgorithm(smudge, new Smear());
		getOperationAlgorithm(smudge, new SpectralShift());

		bind(smudge.getParameter("Source Speed"));
		bind(smudge.getParameter("Downsample"));

		bind(Controller.getInstance().getAppControl("Source Switcher"));
		bind(Controller.getInstance().getAppControl("Source Set Switcher"));
		bind(Controller.getInstance().getAppControl("Save Project"));
	}

	/**
	 * Get a new algorithm wrapping the given operation
	 *
	 * @param smudge
	 *            {@link Smudge}
	 * @param op
	 *            {@link Operation}
	 * @return {@link Algorithm}
	 */
	public Algorithm getOperationAlgorithm(Smudge smudge, Operation op) {
		Algorithm alg = new Algorithm();

		alg.getParameter("Enable").setValue(false);
		bind(alg.getParameter("Enable"));

		alg.add(op);

		smudge.add(alg);

		return alg;
	}

	/**
	 * Create
	 */
	public SkyTestApp() {
		super(projectPath, sourcePath, outputPath, device, overwriteSmudge, deviceServer);

		fullscreenView(fullscreenDisplay);

		if (monitor)
			monitorView();

		if (streaming)
			Controller.getInstance().add(new WebsocketView());

		start();
	}

	/**
	 * Run
	 *
	 * @param args
	 *            unused
	 */
	public static void main(String[] args) {
		new SkyTestApp();
	}

}
