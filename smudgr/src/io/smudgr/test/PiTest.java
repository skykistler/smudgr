package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.Controller;
import io.smudgr.app.view.PiFullscreenView;
import io.smudgr.project.smudge.Smudge;
import io.smudgr.project.smudge.alg.Algorithm;
import io.smudgr.project.smudge.alg.op.DataBend;
import io.smudgr.project.smudge.alg.select.RangeSelect;

public class PiTest extends AppStart {

	static String projectPath = "data/test/test.smudge";

	static boolean newSmudge = true;

	static String sourcePath = "data/test";

	static String outputPath = "data";

	static String device = null;

	static int fullscreenDisplay = 0;
	static boolean monitor = true;

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

		addAutomator("Animate", databend.getParameter("Target"));
	}

	public PiTest() {
		super(projectPath, sourcePath, outputPath, device, newSmudge);

		//		fullscreenView(fullscreenDisplay);
		//		if (monitor)
		//			monitorView();

		Controller.getInstance().add(new PiFullscreenView(fullscreenDisplay));

		start();
	}

	public static void main(String[] args) {
		new PiTest();
	}

}
