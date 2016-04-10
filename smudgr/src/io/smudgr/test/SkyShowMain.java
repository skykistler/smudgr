package io.smudgr.test;

import io.smudgr.controller.BaseController;
import io.smudgr.controller.Controller;
import io.smudgr.controller.controls.AutomateByBeatControl;
import io.smudgr.controller.controls.AutomateByStepControl;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.controls.GifControl;
import io.smudgr.controller.controls.SaveControl;
import io.smudgr.controller.controls.SourceControl;
import io.smudgr.controller.controls.SourceSetControl;
import io.smudgr.ext.midi.MidiExtension;
import io.smudgr.output.ProjectXML;
import io.smudgr.smudge.Smudge;
import io.smudgr.smudge.alg.Algorithm;
import io.smudgr.smudge.alg.coord.ColumnCoords;
import io.smudgr.smudge.alg.coord.ConvergeCoordFunction;
import io.smudgr.smudge.alg.coord.RowCoords;
import io.smudgr.smudge.alg.op.DataBend;
import io.smudgr.smudge.alg.op.PixelShift;
import io.smudgr.smudge.alg.op.PixelSort;
import io.smudgr.smudge.alg.op.SpectralShift;
import io.smudgr.smudge.alg.select.RangeSelect;
import io.smudgr.smudge.source.Image;
import io.smudgr.view.NativeView;

public class SkyShowMain {

	public static Controller make(String filepath) {
		// Declare your controller

		BaseController controller = new BaseController();
		controller.add(new MidiExtension());

		// Make smudge
		Smudge smudge = new Smudge();
		smudge.bind("Enable");
		controller.add(new SourceSetControl("data/venture/noise show"));

		Algorithm sort = new Algorithm();
		sort.bind("Enable");
		sort.getParameter("Enable").setInitial(false);
		sort.add(new ConvergeCoordFunction());

		RangeSelect threshold = new RangeSelect();
		threshold.getParameter("Range Length").setInitial(.1);
		threshold.bind("Range Length");
		sort.add(threshold);

		PixelSort sort_op = new PixelSort();
		sort_op.getParameter("Reverse").setInitial(true);
		sort_op.bind("Reverse");
		sort.add(sort_op);

		smudge.add(sort);

		Algorithm spectral = new Algorithm();
		spectral.bind("Enable");
		spectral.getParameter("Enable").setInitial(false);

		SpectralShift spectral_op = new SpectralShift();
		spectral_op.getParameter("Colors").setInitial(60);
		spectral_op.getParameter("Sort").setInitial(true);
		spectral_op.bind("Colors");
		spectral_op.bind("Palette");
		spectral_op.bind("Sort");
		controller.add(new AutomateByBeatControl(spectral_op.getParameter("Shift")));
		spectral.add(spectral_op);

		smudge.add(spectral);

		Algorithm shift = new Algorithm();
		shift.bind("Enable");
		shift.getParameter("Enable").setInitial(false);
		shift.add(new ConvergeCoordFunction());

		PixelShift shift_op = new PixelShift();
		shift_op.getParameter("Intervals").setInitial(3);
		shift_op.bind("Intervals");
		shift_op.getParameter("Amount").setInitial(.2);
		shift_op.bind("Reverse");
		controller.add(new AutomateByBeatControl(shift_op.getParameter("Amount")));
		shift.add(shift_op);

		smudge.add(shift);

		Algorithm shift1 = new Algorithm();
		shift1.bind("Enable");
		shift1.getParameter("Enable").setInitial(false);
		shift1.add(new ColumnCoords());

		PixelShift shift1_op = new PixelShift();
		shift1_op.getParameter("Intervals").setInitial(3);
		shift1_op.bind("Intervals");
		shift1_op.getParameter("Amount").setInitial(.2);
		shift1_op.bind("Reverse");
		controller.add(new AutomateByBeatControl(shift1_op.getParameter("Amount")));
		shift1.add(shift1_op);

		smudge.add(shift1);

		Algorithm sort1 = new Algorithm();
		sort1.bind("Enable");
		sort1.getParameter("Enable").setInitial(false);
		sort1.add(new ColumnCoords());
		RangeSelect threshold2 = new RangeSelect();
		threshold2.bind("Range Length");
		sort1.add(threshold2);

		PixelSort sort1_op = new PixelSort();
		sort1_op.bind("Reverse");
		sort1.add(sort1_op);

		smudge.add(sort1);

		Algorithm sort2 = new Algorithm();
		sort2.bind("Enable");
		sort2.getParameter("Enable").setInitial(false);
		sort2.add(new RowCoords());
		RangeSelect threshold3 = new RangeSelect();
		threshold3.bind("Range Length");
		sort2.add(threshold3);

		PixelSort sort2_op = new PixelSort();
		sort2_op.bind("Reverse");
		sort2.add(sort2_op);

		smudge.add(sort2);

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
		controller.add(new SaveControl(filepath));
		controller.add(new GifControl("show_record"));
		controller.add(new SourceControl());

		controller.setSmudge(smudge);

		return controller;
	}

	public static Controller load(String filepath) {
		ProjectXML xml = new ProjectXML(filepath);
		return xml.load();
	}

	public static void main(String[] args) {
		Controller c = load("data/show.smudge");

		c.getSmudge().setSource(new Image("data/venture/noise show/lilly 2.png"));

		c.add(new NativeView(-1, true));

		((MidiExtension) c.getExtension("MIDI Extension")).bindDevice("Arturia BeatStepPro");
		c.start();
	}

}
