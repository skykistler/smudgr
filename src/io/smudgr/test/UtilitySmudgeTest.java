package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.controller.Controller;
import io.smudgr.engine.Rack;
import io.smudgr.extensions.image.utility.Utility;
import io.smudgr.extensions.image.utility.color.Painter;
import io.smudgr.extensions.image.utility.crop.Cropper;
import io.smudgr.extensions.image.utility.rotate.Rotator;
import io.smudgr.extensions.image.utility.scale.Scaler;

/**
 * Eric's class for testing the UtilitySmudge
 */
public class UtilitySmudgeTest extends AppStart {

	static String projectPath = "data/test.sproj";

	static boolean overwriteProject = false;

	static String sourcePath = "data/testing";

	static String outputPath = "data/ui_output";

	static String device = "Arturia BeatStep";
	static boolean deviceServer = false;

	static int fullscreenDisplay = -1;
	static boolean monitor = true;

	@Override
	public void buildRack() {
		Rack rack = Controller.getInstance().getProject().getRack();

		Utility util = new Utility();

		Cropper cropper = new Cropper();
		Scaler scaler = new Scaler();
		Painter painter = new Painter();
		Rotator rotator = new Rotator();

		util.add(cropper);
		util.add(scaler);
		util.add(painter);
		util.add(rotator);

		rack.add(util);

		bind(cropper.getParameter("X Offset"));
		bind(cropper.getParameter("Y Offset"));
		bind(cropper.getParameter("X Width"));
		bind(cropper.getParameter("Y Height"));

		bind(scaler.getParameter("Zoom Factor"));

		bind(rotator.getParameter("Rotation Degree"));

		bind(painter.getParameter("Saturation"));
		bind(painter.getParameter("Hue Rotation"));
		bind(painter.getParameter("Brightness"));
	}

	/**
	 *
	 */
	public UtilitySmudgeTest() {
		super(projectPath, sourcePath, outputPath, device, overwriteProject, deviceServer);

		fullscreenView(fullscreenDisplay);
		if (monitor)
			monitorView();

		// Controller.getInstance().add(new WebsocketView());

		start();
	}

	/**
	 * Entry point
	 *
	 * @param args
	 *            {@code String[]}
	 */
	public static void main(String[] args) {
		new UtilitySmudgeTest();
	}

}
