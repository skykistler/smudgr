package io.smudgr.test;

import io.smudgr.controller.BaseController;
import io.smudgr.controller.Controller;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.controls.SaveControl;
import io.smudgr.midi.controller.MidiController;
import io.smudgr.out.ProjectXML;
import io.smudgr.source.Image;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.coord.ColumnCoords;
import io.smudgr.source.smudge.alg.op.Marbeler;
import io.smudgr.source.smudge.alg.op.SpectralShift;
import io.smudgr.source.smudge.alg.select.RangeSelect;
import io.smudgr.view.NativeView;

public class SkyTestMain {

	public static Controller make(String filepath) {
		BaseController controller = new BaseController();
		controller.add(new MidiController());

		Smudge smudge = new Smudge();

		Algorithm alg = new Algorithm();
		alg.add(new ColumnCoords());

		RangeSelect threshold = new RangeSelect();
		threshold.bind("Range Length");
		threshold.bind("Minimum Value");
		alg.add(threshold);

		SpectralShift sort_op = new SpectralShift();
		sort_op.bind("Function");
		sort_op.bind("Shift");
		sort_op.bind("Colors");
		alg.add(sort_op);

		Marbeler marble_op = new Marbeler();
		marble_op.bind("Frequency");
		marble_op.bind("Iterations");
		marble_op.bind("Strength");
		marble_op.bind("Offset - X/Y");
		alg.add(marble_op);

		smudge.add(alg);

		smudge.setController(controller);

		controller.add(new DownsampleControl(1));
		controller.add(new SaveControl(filepath));

		return controller;
	}

	public static Controller load(String filepath) {
		ProjectXML xml = new ProjectXML(filepath);
		return xml.load();
	}

	public static void main(String[] args) {
		Controller c = make("data/test.smudge");
		c.getSmudge().setSource(new Image("data/banner.png"));

		new NativeView(c, 1, false);

		((MidiController) c.getExtensions().get(0)).bindDevice("Arturia BeatStep Pro");
		c.start();
	}
}
