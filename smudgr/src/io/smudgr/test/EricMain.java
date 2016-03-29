package io.smudgr.test;

import io.smudgr.controller.BaseController;
import io.smudgr.controller.Controller;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.controls.SaveControl;
import io.smudgr.controller.controls.SourceControl;
import io.smudgr.controller.controls.SourceSetControl;
import io.smudgr.ext.midi.MidiExtension;
import io.smudgr.out.ProjectXML;
import io.smudgr.source.Image;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.coord.AllCoords;
import io.smudgr.source.smudge.alg.coord.RowCoords;
import io.smudgr.source.smudge.alg.coord.SkewedCoords;
import io.smudgr.source.smudge.alg.op.DataBend;
import io.smudgr.source.smudge.alg.op.PixelSort;
import io.smudgr.source.smudge.alg.select.EdgeSelect;
import io.smudgr.source.smudge.alg.select.RangeSelect;
import io.smudgr.view.NativeView;

public class EricMain {
	public static Controller make(String filepath) {
		// Declare your controller

		BaseController controller = new BaseController();
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

		return controller;
	}

	public static Controller load(String filepath) {
		ProjectXML xml = new ProjectXML(filepath);
		return xml.load();
	}

	public static void main(String[] args) {

		Controller c = load("data/work.smudge");

		c.getSmudge().setSource(new Image("data/work/flowers_source.jpg"));

		new NativeView(c, 0, false);

		((MidiExtension) c.getExtensions().get(0)).bindDevice("Arturia BeatStep");
		c.start();
	}
}
