package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.controller.Controller;
import io.smudgr.engine.Smudge;
import io.smudgr.engine.alg.Algorithm;
import io.smudgr.engine.alg.coord.ColumnCoords;
import io.smudgr.engine.alg.op.PixelSort;
import io.smudgr.engine.alg.select.RangeSelect;
import io.smudgr.extensions.cef.view.WebsocketView;

/**
 * Sky's testing app
 */
public class SkyTestApp extends AppStart {

	static String projectPath = "data/test.smudge";

	static boolean overwriteSmudge = false;

	static String sourcePath = "data/venture/noise show";

	static String outputPath = "data";

	static String device = "Arturia BeatStep Pro";
	static boolean deviceServer = false;

	static int fullscreenDisplay = -1;
	static boolean monitor = true;

	@Override
	public void buildSmudge() {
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		Algorithm alg = new Algorithm();

		RangeSelect range = new RangeSelect();
		alg.add(range);

		PixelSort sort = new PixelSort();
		alg.add(sort);
		alg.add(new ColumnCoords());
		range.getParameter("Range Length").setValue(0);

		// DataBend databend = new DataBend();
		// databend.getParameter("Amount").setValue(2);
		// alg.add(databend);
		// range.getParameter("Range Length").setValue(1);

		smudge.add(alg);

		// AutomatorControl automator1 = addAutomator("Animate",
		// range.getParameter("Range Length"));
		// AutomatorControl automator1 = addAutomator("Animate",
		// databend.getParameter("Target"));

		bind(smudge.getParameter("Source Speed"));
		bind(smudge.getParameter("Downsample"));
		// bind(automator1);
		// bind(range.getParameter("Range Length"));

		bind(Controller.getInstance().getAppControl("Source Switcher"));
		bind(Controller.getInstance().getAppControl("Source Set Switcher"));
		// bind(Controller.getInstance().getAppControl("Record GIF"));
		// bind(Controller.getInstance().getAppControl("Save Project"));
	}

	/**
	 * Create
	 */
	public SkyTestApp() {
		super(projectPath, sourcePath, outputPath, device, overwriteSmudge, deviceServer);

		// fullscreenView(fullscreenDisplay);
		// if (monitor)
		// monitorView();

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
