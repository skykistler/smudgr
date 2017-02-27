package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.util.ProjectSaver;
import io.smudgr.app.view.WebsocketView;
import io.smudgr.engine.Rack;
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

/**
 * Example class for creating a test {@link AppStart} main class
 */
public class SkyTestApp extends AppStart {

	/*
	 * To make your own test class, just duplicate this file and change the
	 * following
	 */

	// where to save/load project file
	static String projectPath = "data/test.sproj";

	// whether to overwrite the existing project file
	static boolean overwriteProject = true;

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
	public void buildRack() {
		Rack rack = Controller.getInstance().getProject().getRack();

		getOperationAlgorithm(rack, new ChannelBleed());
		getOperationAlgorithm(rack, new ChannelCrush());
		getOperationAlgorithm(rack, new ChannelDrift());
		getOperationAlgorithm(rack, new ChannelSort());
		getOperationAlgorithm(rack, new DataBend());
		getOperationAlgorithm(rack, new HSVLModifier());
		getOperationAlgorithm(rack, new Marbeler());
		getOperationAlgorithm(rack, new ParticlePush());
		getOperationAlgorithm(rack, new PixelShift());
		getOperationAlgorithm(rack, new PixelSort());
		getOperationAlgorithm(rack, new RainbowBend());
		getOperationAlgorithm(rack, new Smear());
		getOperationAlgorithm(rack, new SpectralShift());

		bind(rack.getParameter("Source Speed"));
		bind(rack.getParameter("Downsample"));

		bind(Controller.getInstance().getAppControl("Source Switcher"));
		bind(Controller.getInstance().getAppControl("Source Set Switcher"));
		bind(Controller.getInstance().getAppControl("Save Project"));
	}

	/**
	 * Get a new algorithm wrapping the given operation
	 *
	 * @param rack
	 *            {@link Rack}
	 * @param op
	 *            {@link Operation}
	 * @return {@link Algorithm}
	 */
	public Algorithm getOperationAlgorithm(Rack rack, Operation op) {
		Algorithm alg = new Algorithm();

		alg.getParameter("Enable").setValue(false);
		bind(alg.getParameter("Enable"));

		alg.add(op);

		rack.add(alg);

		return alg;
	}

	/**
	 * Create
	 */
	public SkyTestApp() {
		super(projectPath, sourcePath, outputPath, device, overwriteProject, deviceServer);

		fullscreenView(fullscreenDisplay);

		if (monitor)
			monitorView();

		if (streaming)
			Controller.getInstance().add(new WebsocketView());

		start();

		ProjectSaver save = new ProjectSaver();
		save.save();
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
