package io.smudgr.test;

import io.smudgr.controller.BaseController;
import io.smudgr.controller.Controller;
import io.smudgr.controller.controls.AnimateByStepControl;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.controls.SaveControl;
import io.smudgr.midi.controller.MidiController;
import io.smudgr.out.ProjectXML;
import io.smudgr.source.Image;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.coord.ConvergeCoordFunction;
import io.smudgr.source.smudge.alg.op.DataBend;
import io.smudgr.source.smudge.alg.select.RangeSelect;
import io.smudgr.view.NativeView;

public class SkyTestMain {

	public static Controller make(String filepath) {
		BaseController controller = new BaseController();
		controller.add(new MidiController());

		Smudge smudge = new Smudge();

		Algorithm alg = new Algorithm();
		alg.add(new ConvergeCoordFunction());

		RangeSelect threshold = new RangeSelect();
		threshold.bind("Range Length");
		threshold.bind("Minimum Value");
		alg.add(threshold);

		DataBend byte_op = new DataBend();
		byte_op.bind("Amount");
		alg.add(byte_op);
		controller.add(new AnimateByStepControl(byte_op.getParameter("Target Byte")));

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
		c.getSmudge().setSource(new Image("data/nicole.jpg"));

		new NativeView(c, 1, false);

		((MidiController) c.getExtensions().get(0)).bindDevice("Arturia BeatStep Pro");
		c.start();
	}
}
