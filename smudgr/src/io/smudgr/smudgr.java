package io.smudgr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URLDecoder;

import io.smudgr.controller.BaseController;
import io.smudgr.ext.cef.view.CefView;
import io.smudgr.smudge.Smudge;
import io.smudgr.smudge.alg.Algorithm;
import io.smudgr.smudge.alg.coord.ColumnCoords;
import io.smudgr.smudge.alg.op.Marbeler;
import io.smudgr.smudge.alg.op.PixelSort;
import io.smudgr.smudge.alg.select.RangeSelect;

public class smudgr {

	private smudgr(boolean debug) {
		BaseController controller = new BaseController();

		Smudge smudge = new Smudge();
		controller.setSmudge(smudge);

		Algorithm sort = new Algorithm();
		sort.add(new ColumnCoords());

		RangeSelect threshold = new RangeSelect();
		sort.add(threshold);

		sort.add(new PixelSort());
		sort.add(new Marbeler());

		smudge.add(sort);

		controller.add(new CefView(debug));

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
