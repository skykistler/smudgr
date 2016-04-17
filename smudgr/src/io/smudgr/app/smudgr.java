package io.smudgr.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URLDecoder;

import io.smudgr.extensions.cef.view.CefView;
import io.smudgr.project.Project;
import io.smudgr.project.smudge.Smudge;
import io.smudgr.project.smudge.alg.Algorithm;
import io.smudgr.project.smudge.alg.coord.ColumnCoords;
import io.smudgr.project.smudge.alg.op.Marbeler;
import io.smudgr.project.smudge.alg.op.PixelSort;
import io.smudgr.project.smudge.alg.select.RangeSelect;

public class smudgr {

	private smudgr(boolean debug) {
		Controller controller = new Controller();

		Project project = new Project();

		Smudge smudge = new Smudge();
		project.setSmudge(smudge);

		Algorithm sort = new Algorithm();
		sort.add(new ColumnCoords());

		RangeSelect threshold = new RangeSelect();
		sort.add(threshold);

		sort.add(new PixelSort());
		sort.add(new Marbeler());

		smudge.add(sort);

		controller.setProject(project);

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
