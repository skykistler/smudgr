package io.smudgr.test;

import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.device.MidiController;
import io.smudgr.source.Image;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.SmudgeXML;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.coord.ColumnCoords;
import io.smudgr.source.smudge.alg.op.PixelSort;
import io.smudgr.source.smudge.alg.select.RangeSelect;
import io.smudgr.view.JView;

public class SkyTestMain {

	public static Smudge make() {
		Smudge smudge = new Smudge();

		Algorithm sort = new Algorithm();
		sort.add(new ColumnCoords());

		RangeSelect threshold = new RangeSelect();
		threshold.bind("Threshold");
		sort.add(threshold);

		PixelSort sort_op = new PixelSort();
		sort_op.bind("Function");
		sort.add(sort_op);

		smudge.add(sort);

		return smudge;
	}

	public static Smudge load(String filepath) {
		SmudgeXML xml = new SmudgeXML(filepath);
		return xml.load();
	}

	public static void main(String[] args) {
		MidiController controller = new MidiController("test.map");

		Smudge s = load("data/test.smudge");
		s.setSource(new Image("data/nicole.jpg"));
		s.setController(controller);

		new DownsampleControl(controller, 1);

		new JView(controller, -1, true);
		// controller.bindDevice("Arturia BeatStepPro");
		controller.start();

		// s.save("data/test.smudge");
	}
}
