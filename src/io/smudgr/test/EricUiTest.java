package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.controller.Controller;
import io.smudgr.engine.Smudge;
import io.smudgr.engine.alg.Algorithm;
import io.smudgr.engine.alg.op.ChannelSort;
import io.smudgr.engine.alg.op.ParticlePush;
import io.smudgr.engine.alg.select.RangeSelect;
import io.smudgr.extensions.cef.view.WebsocketView;

/**
 * Eric's class for testing UI
 */
public class EricUiTest extends AppStart {

	static String projectPath = "data/test.smudge";

	static boolean overwriteSmudge = false;

	static String sourcePath = "data/ui_sources";

	static String outputPath = "data/ui_output";

	static String device = "Arturia BeatStep";
	static boolean deviceServer = false;

	static int fullscreenDisplay = -1;
	static boolean monitor = true;

	@Override
	public void buildSmudge() {
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		Algorithm alg = new Algorithm();
		RangeSelect range = new RangeSelect();
		alg.add(range);
		range.getParameter("Range Length").setValue(0);
		ChannelSort sort = new ChannelSort();
		alg.add(sort);
		// range.getParameter("Range Length").setValue(1);

		smudge.add(alg);

		Algorithm alg2 = new Algorithm();
		alg2.add(new ParticlePush());
		smudge.add(alg2);

		// AutomatorControl automator1 = addAutomator("Animate",
		// range.getParameter("Range Length"));
		// AutomatorControl automator1 = addAutomator("Animate",
		// databend.getParameter("Target"));

		// bind(smudge.getParameter("Source Speed"));
		// bind(automator1);
		// bind(range.getParameter("Range Length"));

		smudge.getParameter("Downsample").setValue(1);

		bind(Controller.getInstance().getAppControl("Source Switcher"));
		bind(Controller.getInstance().getAppControl("Source Set Switcher"));
		bind(Controller.getInstance().getAppControl("Record GIF"));
		// bind(Controller.getInstance().getAppControl("Save Project"));
	}

	/**
	 * 
	 */
	public EricUiTest() {
		super(projectPath, sourcePath, outputPath, device, overwriteSmudge, deviceServer);

		// fullscreenView(fullscreenDisplay);
		// if (monitor)
		// monitorView();

		Controller.getInstance().add(new WebsocketView());

		start();
	}

	/**
	 * Entry point
	 * 
	 * @param args
	 *            {@code String[]}
	 */
	public static void main(String[] args) {
		new EricUiTest();
	}

}
