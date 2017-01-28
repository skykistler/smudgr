package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.controller.Controller;
import io.smudgr.app.view.PiFullscreenView;
import io.smudgr.engine.Smudge;
import io.smudgr.engine.alg.Algorithm;
import io.smudgr.engine.alg.op.DataBend;
import io.smudgr.engine.alg.select.RangeSelect;

/**
 * Used for testing a raspberry pi
 */
public class PiTest extends AppStart {

	static String projectPath = "data/nano_pad.smudge";

	static boolean overwriteSmudge = false;

	static String sourcePath = "data/test";

	static String outputPath = "data";

	static String device = isMac() ? "PAD" : "nanoPAD2";
	static boolean deviceServer = false;

	static int fullscreenDisplay = 0;
	static boolean monitor = !isLinux();

	@Override
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

		// AutomatorControl a = addAutomator("Animate",
		// databend.getParameter("Target"));

		bind(databend.getParameter("Amount"), true, 16);
		bind(databend.getParameter("Target"), true, 16);

		bind(Controller.getInstance().getAppControl("Save Project"), false, 16);
	}

	/**
	 * Create
	 */
	public PiTest() {
		super(projectPath, sourcePath, outputPath, device, overwriteSmudge, deviceServer);

		getMidi().bindDevice(device, deviceServer);

		if (monitor)
			monitorView();
		else
			Controller.getInstance().add(new PiFullscreenView(fullscreenDisplay));

		start();
	}

	/**
	 * Run
	 *
	 * @param args
	 *            unused
	 */
	public static void main(String[] args) {
		new PiTest();
	}

}
