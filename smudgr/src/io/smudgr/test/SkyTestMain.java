package io.smudgr.test;

import io.smudgr.app.ProjectXML;
import io.smudgr.controller.Controller;
import io.smudgr.controller.controls.AutomateByStepControl;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.controls.RecordGifControl;
import io.smudgr.controller.controls.SaveProjectControl;
import io.smudgr.ext.midi.MidiExtension;
import io.smudgr.smudge.Smudge;
import io.smudgr.smudge.alg.Algorithm;
import io.smudgr.smudge.alg.op.DataBend;
import io.smudgr.smudge.alg.select.RangeSelect;
import io.smudgr.smudge.source.Image;
import io.smudgr.view.NativeView;

public class SkyTestMain {

	public static void make(String filepath) {
		Controller controller = new Controller();
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

		controller.add(new DownsampleControl(1));
		controller.add(new RecordGifControl("test"));
		controller.add(new SaveProjectControl(filepath));
		controller.setSmudge(smudge);
	}

	public static void load(String filepath) {
		ProjectXML xml = new ProjectXML(filepath);
		xml.load();
	}

	public static void main(String[] args) {
		make("data/test.smudge");

		Controller c = Controller.getInstance();
		c.getSmudge().setSource(new Image("data/texture.png"));

		c.add(new NativeView(-1, true));

		((MidiExtension) c.getExtension("MIDI Extension")).bindDevice("Arturia BeatStepPro");
		c.start();
	}
}
