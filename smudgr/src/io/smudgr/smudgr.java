package io.smudgr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URLDecoder;

import io.smudgr.controller.Controller;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.coord.ColumnCoords;
import io.smudgr.source.smudge.alg.op.Marbeler;
import io.smudgr.source.smudge.alg.op.PixelSort;
import io.smudgr.source.smudge.alg.select.ThresholdSelect;
import io.smudgr.view.cef.CefView;

public class smudgr {

	private smudgr(boolean debug) {
		Controller controller = new Controller();

		Smudge smudge = new Smudge();
		controller.setSmudge(smudge);

		Algorithm sort = new Algorithm();
		sort.add(new ColumnCoords());

		ThresholdSelect threshold = new ThresholdSelect();
		sort.add(threshold);

		sort.add(new PixelSort());
		sort.add(new Marbeler());

		smudge.add(sort);

		new CefView(controller, debug);

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

			new smudgr(debug);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
