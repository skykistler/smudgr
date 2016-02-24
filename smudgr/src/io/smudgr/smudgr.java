package io.smudgr;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import io.smudgr.controller.SmudgeController;
import io.smudgr.source.Image;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.Marbeler;
import io.smudgr.view.cef.CefView;

public class smudgr {

	private smudgr() {
		SmudgeController controller = new SmudgeController();

		Smudge smudge = new Smudge(new Image("lilly.png"));

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
				PrintStream ps = new PrintStream(new FileOutputStream("smudgr_errors.log"));
				System.setErr(ps);
				System.setOut(ps);
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
