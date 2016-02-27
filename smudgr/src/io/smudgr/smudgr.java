package io.smudgr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URLDecoder;

import io.smudgr.controller.Controller;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.Marbeler;
import io.smudgr.view.cef.CefView;

public class smudgr {

	private smudgr() {
		Controller controller = new Controller();

		Smudge smudge = new Smudge();
		controller.setSmudge(smudge);

		Marbeler m = new Marbeler(smudge);
		m.bind("Offset - X/Y");
		m.bind("Frequency");
		m.bind("Iterations");
		m.bind("Strength");

		new CefView(controller);

		controller.start();
	}

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

			new smudgr();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
