package io.smudgr.test;

import io.smudgr.controller.controls.AnimateOnBeatControl;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.controls.SourceSetControl;
import io.smudgr.controller.device.MidiController;
import io.smudgr.source.Gif;
import io.smudgr.source.Image;
import io.smudgr.source.Source;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.coord.ColumnCoords;
import io.smudgr.source.smudge.alg.coord.ConvergeCoordFunction;
import io.smudgr.source.smudge.alg.coord.RowCoords;
import io.smudgr.source.smudge.alg.op.PixelShift;
import io.smudgr.source.smudge.alg.op.PixelSort;
import io.smudgr.source.smudge.alg.op.SourceMixerHack;
import io.smudgr.source.smudge.alg.op.SpectralShift;
import io.smudgr.view.JView;

public class SkyPCPMain {

	public static void main(String[] args) {
		// Declare your controller
		MidiController controller = new MidiController("bbt_pcp.map");

		// Make smudge
		Smudge smudge = new Smudge();
		new SourceSetControl(controller, "pcp/sets/");

		Algorithm sort = new Algorithm();
		sort.bind("Enable");
		sort.getParameter("Enable").setInitial(false);
		sort.add(new ConvergeCoordFunction());
		PixelSort sort_op = new PixelSort();
		sort_op.getParameter("Threshold").setInitial(.1);
		sort_op.getParameter("Reverse").setInitial(true);
		sort_op.bind("Threshold");
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
		new AnimateOnBeatControl(controller, spectral_op.getParameter("Shift"));
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
		new AnimateOnBeatControl(controller, shift_op.getParameter("Amount"));
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
		new AnimateOnBeatControl(controller, shift1_op.getParameter("Amount"));
		shift1.add(shift_op);
		smudge.add(shift1);

		Algorithm sort1 = new Algorithm();
		sort1.bind("Enable");
		sort1.getParameter("Enable").setInitial(false);
		sort1.add(new ColumnCoords());
		PixelSort sort1_op = new PixelSort();
		sort1.bind("Threshold");
		sort1.bind("Reverse");
		sort1.add(sort1_op);
		smudge.add(sort1);

		Algorithm sort2 = new Algorithm();
		sort2.add(new RowCoords());
		PixelSort sort2_op = new PixelSort();
		sort2_op.bind("Threshold");
		sort2_op.bind("Reverse");
		sort2_op.bind("Enable");
		sort2_op.getParameter("Enable").setInitial(false);
		sort2.add(sort2_op);
		smudge.add(sort2);

		makeSourceMixer(smudge, "&.png");
		makeSourceMixer(smudge, "disco.png");
		makeSourceMixer(smudge, "fuck.png");
		makeSourceMixer(smudge, "it.png");
		makeSourceMixer(smudge, "keep.png");
		makeSourceMixer(smudge, "mellow.png");
		makeSourceMixer(smudge, "mother.png");
		makeSourceMixer(smudge, "rock.png");
		makeSourceMixer(smudge, "roll.png");
		makeSourceMixer(smudge, "giphy-2.gif");

		new DownsampleControl(controller, 1);

		// Declare your view
		new JView(controller, 0, false);

		controller.setSmudge(smudge);
		controller.bindDevice("User Port");
		controller.bindDevice("Arturia BeatStepPro");
		controller.start();
	}

	public static void makeSourceMixer(Smudge s, String path) {
		path = "pcp/stills/" + path;
		Source src = null;
		if (path.endsWith(".gif"))
			src = new Gif(path);
		else
			src = new Image(path);

		Algorithm sm = new Algorithm();
		sm.bind("Enable");
		sm.getParameter("Enable").setInitial(false);

		SourceMixerHack sm_op = new SourceMixerHack(src);
		sm.add(sm_op);

		s.add(sm);
	}

}
