package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.Controller;
import io.smudgr.app.view.PiFullscreenView;
import io.smudgr.project.smudge.Smudge;
import io.smudgr.project.smudge.alg.Algorithm;
import io.smudgr.project.smudge.alg.op.DataBend;
import io.smudgr.project.smudge.alg.select.RangeSelect;

public class PiTest extends AppStart {

	static String projectPath = "data/nano_pad.smudge";

	static boolean overwriteSmudge = false;

	static String sourcePath = "data/test";

	static String outputPath = "data";

	static String device = isLinux() ? "nanoPAD2 [hw:1,0,0]" : "PAD";

	static int fullscreenDisplay = 0;
	static boolean monitor = !isLinux();

	public void buildSmudge() {
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		Algorithm alg = new Algorithm();

		RangeSelect range = new RangeSelect();
		alg.add(range);

		DataBend databend = new DataBend();
		databend.getParameter("Amount").setValue(2);
		alg.add(databend);
		range.getParameter("Range Length").setValue(1);

		smudge.add(alg);

		//		AutomatorControl a = addAutomator("Animate", databend.getParameter("Target"));

		bind(databend.getParameter("Amount"), true, 16);
		bind(databend.getParameter("Target"), true, 16);

		bind(Controller.getInstance().getAppControl("Save Project"));
	}

	public PiTest() {
		super(projectPath, sourcePath, outputPath, device, overwriteSmudge);

		if (monitor)
			monitorView();
		else
			Controller.getInstance().add(new PiFullscreenView(fullscreenDisplay));

		start();
	}

	public static void main(String[] args) {
		new PiTest();
	}

}
