package io.smudgr.test;

import io.smudgr.app.ProjectXML;
import io.smudgr.controller.Controller;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.controls.SaveControl;
import io.smudgr.controller.controls.SourceControl;
import io.smudgr.controller.controls.SourceSetControl;
import io.smudgr.ext.midi.MidiExtension;
import io.smudgr.smudge.Smudge;
import io.smudgr.smudge.alg.Algorithm;
import io.smudgr.smudge.alg.coord.RowCoords;
import io.smudgr.smudge.alg.op.DataBend;
import io.smudgr.smudge.alg.select.EdgeSelect;
import io.smudgr.smudge.alg.select.RangeSelect;
import io.smudgr.smudge.source.Image;

public class EricMain {
	public static void make(String filepath) {
		// Declare your controller

		Controller controller = new Controller();
		controller.add(new MidiExtension());

		// Make smudge
		Smudge smudge = new Smudge();
		smudge.bind("Enable");
		controller.add(new SourceSetControl("data/work"));

		Algorithm alg1 = new Algorithm();
		alg1.bind("Enable");
		alg1.getParameter("Enable").setInitial(false);
		alg1.add(new RowCoords());

		DataBend br = new DataBend();
		br.bind("Enable");
		br.bind("Precursor");
		br.bind("Substitute Value");
		br.bind("Amount");

		alg1.add(br);

		RangeSelect threshold = new RangeSelect();
		threshold.getParameter("Range Length").setInitial(1.0);
		threshold.bind("Range Length");
		alg1.add(threshold);

		EdgeSelect e_select = new EdgeSelect();
		e_select.bind("Function");
		e_select.bind("Max Edge Strength");
		e_select.bind("Direction");
		alg1.add(e_select);

		smudge.add(alg1);

		controller.add(new DownsampleControl(1));
		controller.add(new SaveControl(filepath));
		controller.add(new SourceControl());

		controller.setSmudge(smudge);
	}

	public static void load(String filepath) {
		ProjectXML xml = new ProjectXML(filepath);
		xml.load();
	}

	public static void main(String[] args) {
		load("data/work.smudge");

		Controller c = Controller.getInstance();
		c.getSmudge().setSource(new Image("data/work/flowers_source.jpg"));

		((MidiExtension) c.getExtension("MIDI Extension")).bindDevice("Arturia BeatStep");
		c.start();
	}
}
