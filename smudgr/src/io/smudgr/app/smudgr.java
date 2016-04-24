package io.smudgr.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URLDecoder;

import io.smudgr.extensions.automate.controls.AutomatorControl;
import io.smudgr.extensions.cef.view.CefView;
import io.smudgr.project.smudge.Smudge;
import io.smudgr.project.smudge.alg.Algorithm;
import io.smudgr.project.smudge.alg.op.DataBend;
import io.smudgr.project.smudge.alg.select.RangeSelect;

public class smudgr extends AppStart {

	public static void main(String[] args) {
		boolean debug = true;
		try {
			if (debug) {
				String this_path = URLDecoder.decode(smudgr.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
				String parent_path = (new File(this_path)).getParentFile().getAbsolutePath();

				System.setErr(new PrintStream(new FileOutputStream(parent_path + "/smudgr_errors.log")));
				System.setOut(new PrintStream(new FileOutputStream(parent_path + "/smudgr.log")));
			} else {
				PrintStream nullStream = new PrintStream(new OutputStream() {
					public void write(int b) {
					}
				});
				System.setErr(nullStream);
				System.setOut(nullStream);
			}

			new smudgr(debug);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private smudgr(boolean debug) {
		super("", "", "", "Arturia BeatStep Pro");

		Controller.getInstance().add(new CefView(debug));

		start(true);
	}

	public void buildSmudge() {
		// Pro-tip: In eclipse, you can Ctrl+Click on a class name to quickly open that class
		// There, you can see the names of available parameters
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		// Put algorithm/smudge building stuff here
		Algorithm alg = new Algorithm();

		// Example Selector
		RangeSelect range = new RangeSelect();
		alg.add(range);

		// Example operation
		DataBend databend = new DataBend();
		alg.add(databend);

		// Make sure to add any new algorithms to the smudge
		smudge.add(alg);

		// This is how you make an automated thingy, I've included a method writing this easier
		AutomatorControl automator1 = addAutomator("Animate", databend.getParameter("Target"));

		bind(smudge.getParameter("Downsample"));
		bind(databend.getParameter("Amount"));
		bind(automator1);
		bind(range.getParameter("Range Length"));

		bind(Controller.getInstance().getAppControl("Source Set Switcher"));
		bind(Controller.getInstance().getAppControl("Source Switcher"));
		bind(Controller.getInstance().getAppControl("Record GIF"));
		bind(Controller.getInstance().getAppControl("Save Project"));
	}

}
