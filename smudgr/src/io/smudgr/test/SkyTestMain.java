package io.smudgr.test;

import io.smudgr.app.Controller;
import io.smudgr.app.controls.AutomateByStepControl;
import io.smudgr.app.controls.DownsampleControl;
import io.smudgr.app.controls.RecordGifControl;
import io.smudgr.app.controls.SaveProjectControl;
import io.smudgr.extensions.midi.MidiExtension;
import io.smudgr.project.ProjectXML;
import io.smudgr.smudge.Smudge;
import io.smudgr.smudge.alg.Algorithm;
import io.smudgr.smudge.alg.bound.Bound;
import io.smudgr.smudge.alg.op.DataBend;
import io.smudgr.smudge.alg.select.RangeSelect;
import io.smudgr.smudge.source.Image;
import io.smudgr.view.NativeView;

public class SkyTestMain {

	public static void make(String filepath) {
		Controller controller = new Controller();
		controller.add(new MidiExtension());

		Smudge smudge = new Smudge();

		Algorithm alg = new Algorithm();
		alg.bind("Enable");
		alg.getParameter("Enable").setInitial(false);

		Bound bound = new Bound();
		bound.bind("Bound X");
		bound.bind("Bound Y");
		bound.bind("Bound Width");
		bound.bind("Bound Height");
		alg.add(bound);

		RangeSelect threshold = new RangeSelect();
		threshold.bind("Range Length");
		alg.add(threshold);

		DataBend byte_op = new DataBend();
		byte_op.bind("Amount");
		alg.add(byte_op);
		controller.add(new AutomateByStepControl(byte_op.getParameter("Target")));

		smudge.add(alg);

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
		load("data/test.smudge");

		Controller c = Controller.getInstance();
		c.getSmudge().setSource(new Image("data/lilly.png"));

		c.add(new NativeView(-1, true));

		((MidiExtension) c.getExtension("MIDI Extension")).bindDevice("Arturia BeatStepPro");
		c.start();
	}
}
