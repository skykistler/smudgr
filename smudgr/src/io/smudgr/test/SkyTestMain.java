package io.smudgr.test;

import io.smudgr.controller.BaseController;
import io.smudgr.controller.Controller;
import io.smudgr.controller.controls.AutomateByStepControl;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.controls.GifControl;
import io.smudgr.controller.controls.SaveControl;
import io.smudgr.ext.midi.MidiExtension;
import io.smudgr.output.ProjectXML;
import io.smudgr.source.Image;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.op.DataBend;
import io.smudgr.source.smudge.alg.select.RangeSelect;
import io.smudgr.view.NativeView;

public class SkyTestMain {

	public static Controller make(String filepath) {
		BaseController controller = new BaseController();
		controller.add(new MidiExtension());

		Smudge smudge = new Smudge();

		Algorithm byteRep = new Algorithm();
		byteRep.bind("Enable");
		byteRep.getParameter("Enable").setInitial(false);
		RangeSelect threshold4 = new RangeSelect();
		threshold4.bind("Range Length");
		byteRep.add(threshold4);

		DataBend byte_op = new DataBend();
		byte_op.bind("Amount");
		byteRep.add(byte_op);
		controller.add(new AutomateByStepControl(byte_op.getParameter("Target")));

		smudge.add(byteRep);

		smudge.setController(controller);

		controller.add(new DownsampleControl(1));
		controller.add(new GifControl("test"));
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

		new NativeView(c, -1, true);

		((MidiExtension) c.getExtensions().get(0)).bindDevice("Arturia BeatStepPro");
		c.start();
	}
}
