package io.smudgr.test;

import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.device.MidiController;
import io.smudgr.source.Image;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.op.PixelSort;
import io.smudgr.view.JView;

public class SkyTestMain {
	public static void main(String[] args) {
		MidiController controller = new MidiController("test.map");

		Smudge smudge = new Smudge();
		controller.setSmudge(smudge);
		smudge.setSource(new Image("nicole.jpg"));

		Algorithm basicSort = new Algorithm();
		PixelSort sort_op = new PixelSort();
		basicSort.add(sort_op);
		smudge.add(basicSort);

		new DownsampleControl(controller, 1);

		new JView(controller, -1, true);

		//		controller.bindDevice("Arturia BeatStep Pro");
		controller.start();
	}
}
