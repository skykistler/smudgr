package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.Controller;
import io.smudgr.project.smudge.Smudge;
import io.smudgr.project.smudge.alg.Algorithm;
import io.smudgr.project.smudge.alg.coord.ColumnCoords;
import io.smudgr.project.smudge.alg.op.PixelSort;
import io.smudgr.project.smudge.alg.select.RangeSelect;

public class SkyTestApp extends AppStart {

	static String projectPath = "data/test.smudge";

	static boolean newSmudge = true;

	static String sourcePath = "data/venture/oceans";

	static String outputPath = "data";

	static String device = "Arturia BeatStepPro";

	static int fullscreenDisplay = -1;
	static boolean monitor = true;

	public void buildSmudge() {
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		Algorithm alg = new Algorithm();

		RangeSelect range = new RangeSelect();
		alg.add(range);

		//		DataBend databend = new DataBend();
		//		alg.add(databend);

		PixelSort sort = new PixelSort();
		alg.add(sort);

		alg.add(new ColumnCoords());

		smudge.add(alg);

		//		AutomatorControl automator1 = addAutomator("Animate", databend.getParameter("Target"));

		bind(smudge.getParameter("Downsample"));
		//		bind(databend.getParameter("Amount"));
		//		bind(automator1);
		bind(range.getParameter("Range Length"));

		bind(Controller.getInstance().getAppControl("Source Set Switcher"));
		bind(Controller.getInstance().getAppControl("Source Switcher"));
		bind(Controller.getInstance().getAppControl("Record GIF"));
		bind(Controller.getInstance().getAppControl("Save Project"));
	}

	public SkyTestApp() {
		super(projectPath, sourcePath, outputPath, device, newSmudge);

		fullscreenView(fullscreenDisplay);
		if (monitor)
			monitorView();

		start();
	}

	public static void main(String[] args) {
		new SkyTestApp();
	}

}
