package io.smudgr.test;

import io.smudgr.controller.BaseController;
import io.smudgr.controller.Controller;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.controls.SaveControl;
import io.smudgr.controller.controls.SourceControl;
import io.smudgr.controller.controls.SourceSetControl;
import io.smudgr.midi.controller.MidiController;
import io.smudgr.out.ProjectXML;
import io.smudgr.source.Image;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.coord.AllCoords;
import io.smudgr.source.smudge.alg.coord.SkewedCoords;
import io.smudgr.source.smudge.alg.op.ByteReplace;
import io.smudgr.source.smudge.alg.op.PixelSort;

import io.smudgr.source.smudge.alg.select.RangeSelect;
import io.smudgr.view.NativeView;

public class EricMain {
	public static Controller make(String filepath) {
		// Declare your controller

		BaseController controller = new BaseController();
		controller.add(new MidiController());

		// Make smudge
		Smudge smudge = new Smudge();
		smudge.bind("Enable");
		controller.add(new SourceSetControl("data/work"));

		Algorithm alg1 = new Algorithm();
		alg1.bind("Enable");
		alg1.getParameter("Enable").setInitial(false);
		alg1.add(new AllCoords());
	
		ByteReplace br = new ByteReplace();
		br.bind("Enable");
		br.bind("Precursor");
		br.bind("Substitute 1");
		br.bind("Substitute 2");
		
		alg1.add(br);
		
//		SkewedCoords coords = new SkewedCoords();
//		coords.bind("Skew Degree");
//		sort.add(coords);
		
//		RangeSelect threshold = new RangeSelect();
//		threshold.getParameter("Range Length").setInitial(.1);
//		threshold.bind("Range Length");
//		sort.add(threshold);

//		PixelSort sort_op = new PixelSort();
//		sort_op.getParameter("Reverse").setInitial(true);
//		sort_op.bind("Reverse");
//		sort.add(sort_op);

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

		Controller c = make("data/work.smudge");

		c.getSmudge().setSource(new Image("data/work/flowers_source.jpg"));

		new NativeView(c, 0, false);

		((MidiController) c.getExtensions().get(0)).bindDevice("Arturia BeatStep");
		c.start();
	}
}
