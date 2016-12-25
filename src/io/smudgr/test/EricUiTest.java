package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.Controller;
import io.smudgr.extensions.cef.view.WebsocketView;
import io.smudgr.project.smudge.Smudge;
import io.smudgr.project.smudge.alg.Algorithm;
import io.smudgr.project.smudge.alg.op.DataBend;
import io.smudgr.project.smudge.alg.select.RangeSelect;

public class EricUiTest extends AppStart {

	static String projectPath = "data/test.smudge";

	static boolean overwriteSmudge = false;

	static String sourcePath = "data/ui_sources";

	static String outputPath = "data/ui_output";

	static String device = "Arturia BeatStep";
	static boolean deviceServer = false;

	static int fullscreenDisplay = -1;
	static boolean monitor = true;

	public void buildSmudge() {
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		Algorithm alg = new Algorithm();

		RangeSelect range = new RangeSelect();
		alg.add(range);

		range.getParameter("Range Length").setValue(0);

		DataBend databend = new DataBend();
		databend.getParameter("Amount").setValue(2);
		alg.add(databend);
		range.getParameter("Range Length").setValue(1);

		smudge.add(alg);

		// AutomatorControl automator1 = addAutomator("Animate",
		// range.getParameter("Range Length"));
		// AutomatorControl automator1 = addAutomator("Animate",
		// databend.getParameter("Target"));

		// bind(smudge.getParameter("Source Speed"));
		bind(smudge.getParameter("Downsample"));
		// bind(automator1);
		// bind(range.getParameter("Range Length"));

		smudge.getParameter("Downsample").setValue(0.6);

		bind(Controller.getInstance().getAppControl("Source Switcher"));
		bind(Controller.getInstance().getAppControl("Source Set Switcher"));
		bind(Controller.getInstance().getAppControl("Record GIF"));
		// bind(Controller.getInstance().getAppControl("Save Project"));
	}

	public EricUiTest() {
		super(projectPath, sourcePath, outputPath, device, overwriteSmudge, deviceServer);

		// fullscreenView(fullscreenDisplay);
		// if (monitor)
		// monitorView();

		Controller.getInstance().add(new WebsocketView());

		start();
	}

	public static void main(String[] args) {
		new EricUiTest();
	}

}
