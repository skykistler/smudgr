package io.smudgr;

import io.smudgr.alg.PixelShift;
import io.smudgr.alg.PixelSort;
import io.smudgr.alg.SpectralShift;
import io.smudgr.alg.coord.ColumnCoords;
import io.smudgr.alg.coord.ConvergeCoordFunction;
import io.smudgr.alg.coord.RowCoords;
import io.smudgr.controller.controls.AnimateOnBeatControl;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.controls.EnableSmudgeControl;
import io.smudgr.controller.device.MidiController;
import io.smudgr.source.Image;
import io.smudgr.view.JView;

public class SkyPCPMain {

	public static void main(String[] args) {
		// Declare your controller
		MidiController controller = new MidiController("bbt_pcp.map");

		// Make smudge
		Smudge smudge = new Smudge(new Image("lilly 2.png"));
		//		new SourceSetControl(controller, "dankGifs3");
		new EnableSmudgeControl(controller);

		// Set smudge before doing anything
		controller.setSmudge(smudge);

		PixelSort sort = new PixelSort(smudge);
		sort.setCoordFunction(new ConvergeCoordFunction());
		sort.getParameter("Threshold").setInitial(.1);
		sort.getParameter("Reverse").setInitial(true);
		sort.bind("Threshold");
		sort.bind("Reverse");
		sort.bind("Enable");
		sort.getParameter("Enable").setInitial(false);

		SpectralShift spectral = new SpectralShift(smudge);
		spectral.getParameter("Colors").setInitial(60);
		spectral.getParameter("Sort").setInitial(true);
		spectral.bind("Colors");
		spectral.bind("Palette");
		spectral.bind("Sort");
		spectral.bind("Enable");
		spectral.getParameter("Enable").setInitial(false);
		new AnimateOnBeatControl(controller, spectral.getParameter("Shift"));

		PixelShift shift = new PixelShift(smudge);
		shift.setCoordFunction(new ConvergeCoordFunction());
		shift.getParameter("Intervals").setInitial(3);
		shift.bind("Intervals");
		shift.getParameter("Amount").setInitial(.2);
		shift.bind("Enable");
		shift.getParameter("Enable").setInitial(false);
		shift.bind("Reverse");
		new AnimateOnBeatControl(controller, shift.getParameter("Amount"));

		PixelShift shift1 = new PixelShift(smudge);
		shift1.setCoordFunction(new ColumnCoords());
		shift1.getParameter("Intervals").setInitial(3);
		shift1.bind("Intervals");
		shift1.getParameter("Amount").setInitial(.2);
		shift1.bind("Enable");
		shift1.getParameter("Enable").setInitial(false);
		shift1.bind("Reverse");
		new AnimateOnBeatControl(controller, shift1.getParameter("Amount"));

		PixelSort sort1 = new PixelSort(smudge);
		sort1.setCoordFunction(new ColumnCoords());
		sort1.bind("Threshold");
		sort1.bind("Reverse");
		sort1.bind("Enable");
		sort1.getParameter("Enable").setInitial(false);

		PixelSort sort2 = new PixelSort(smudge);
		sort2.setCoordFunction(new RowCoords());
		sort2.bind("Threshold");
		sort2.bind("Reverse");
		sort2.bind("Enable");
		sort2.getParameter("Enable").setInitial(false);

		// ChannelCrush smear = new ChannelCrush(smudge);
		// smear.getParameter("Green Shift").setInitial(7);
		// smear.getParameter("Blue Shift").setInitial(7);
		// smear.getParameter("Red Mask").setInitial(0);
		// smear.bind("Enable");
		// smear.getParameter("Enable").setInitial(false);
		//
		// ChannelCrush bsmear = new ChannelCrush(smudge);
		// bsmear.getParameter("Green Shift").setInitial(7);
		// bsmear.getParameter("Red Shift").setInitial(7);
		// bsmear.getParameter("Blue Mask").setInitial(0);
		// bsmear.bind("Enable");
		// bsmear.getParameter("Enable").setInitial(false);
		//
		// ChannelCrush gsmear = new ChannelCrush(smudge);
		// gsmear.getParameter("Red Shift").setInitial(7);
		// gsmear.getParameter("Blue Shift").setInitial(7);
		// gsmear.getParameter("Green Mask").setInitial(0);
		// gsmear.bind("Enable");
		// gsmear.getParameter("Enable").setInitial(false);

		new DownsampleControl(controller, 1);

		// Declare your view
		new JView(controller, 0);

		controller.bindDevice("User Port");
		controller.bindDevice("Arturia BeatStepPro");
		controller.start();
	}

}
