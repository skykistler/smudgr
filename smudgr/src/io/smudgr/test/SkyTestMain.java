package io.smudgr.test;

import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.device.MidiController;
import io.smudgr.source.Image;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.coord.ColumnCoords;
import io.smudgr.source.smudge.alg.op.PixelSort;
import io.smudgr.source.smudge.alg.select.ThresholdSelect;
import io.smudgr.view.JView;

public class SkyTestMain {
	public static void main(String[] args) {
		MidiController controller = new MidiController("test.map");

		Smudge smudge = new Smudge();
		controller.setSmudge(smudge);
		smudge.setSource(new Image("nicole.jpg"));

		Algorithm sort = new Algorithm();
		sort.add(new ColumnCoords());

		ThresholdSelect threshold = new ThresholdSelect();
		threshold.bind("Threshold");
		sort.add(threshold);

		PixelSort sort_op = new PixelSort();
		sort_op.bind("Function");
		sort.add(sort_op);

		smudge.add(sort);

		new DownsampleControl(controller, 1);

		new JView(controller, -1, true);

		controller.bindDevice("Arturia BeatStepPro");
		controller.start();
	}
}
