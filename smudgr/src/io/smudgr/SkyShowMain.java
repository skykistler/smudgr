package io.smudgr;

import io.smudgr.alg.ChannelCrush;
import io.smudgr.alg.PixelShift;
import io.smudgr.alg.PixelSort;
import io.smudgr.alg.SpectralShift;
import io.smudgr.alg.coord.ColumnCoords;
import io.smudgr.alg.coord.ConvergeCoordFunction;
import io.smudgr.alg.coord.RowCoords;
import io.smudgr.controller.controls.AnimationControl;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.controls.SourceSwitcherControl;
import io.smudgr.controller.device.MidiController;
import io.smudgr.view.JView;

public class SkyShowMain {

	public static void main(String[] args) {
		// Declare your controller
		MidiController controller = new MidiController(11);

		// Make a smudge
		Smudge smudge = new Smudge("test", "digital shamans/a venture.png");
		new SourceSwitcherControl(controller, "digital shamans");

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
		new AnimationControl(controller, spectral.getParameter("Shift"), .005);

		PixelShift shift = new PixelShift(smudge);
		shift.setCoordFunction(new ConvergeCoordFunction());
		shift.getParameter("Intervals").setInitial(3);
		shift.bind("Intervals");
		shift.getParameter("Amount").setInitial(.2);
		shift.bind("Enable");
		shift.getParameter("Enable").setInitial(false);
		new AnimationControl(controller, shift.getParameter("Amount"), .001);

		PixelShift shift1 = new PixelShift(smudge);
		shift1.setCoordFunction(new ColumnCoords());
		shift1.getParameter("Intervals").setInitial(3);
		shift1.bind("Intervals");
		shift1.getParameter("Amount").setInitial(.2);
		shift1.bind("Enable");
		shift1.getParameter("Enable").setInitial(false);
		new AnimationControl(controller, shift1.getParameter("Amount"));

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

		ChannelCrush smear = new ChannelCrush(smudge);
		smear.getParameter("Green Shift").setInitial(7);
		smear.getParameter("Blue Shift").setInitial(7);
		smear.getParameter("Red Mask").setInitial(0);
		smear.bind("Enable");
		smear.getParameter("Enable").setInitial(false);

		new DownsampleControl(controller, 1);

		// Declare your view
		new JView(controller, 0);

		controller.bindDevice("Arturia BeatStepPro");
		controller.start();
	}

}
