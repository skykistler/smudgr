package io.smudgr;

import io.smudgr.alg.PixelShift;
import io.smudgr.alg.coord.ColumnCoords;
import io.smudgr.controller.controls.AnimateByStepControl;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.device.MidiController;
import io.smudgr.source.Image;
import io.smudgr.view.JView;

public class EricMain {
	public static void main(String[] args) {
		// Declare your controller
		MidiController controller = new MidiController(11);

		// Make a smudge
		Smudge smudge = new Smudge(new Image("emma.jpg"));
		//new VideoControl(controller, "cars.mp4", 300);

		// Set smudge before doing anything
		controller.setSmudge(smudge);

		//		BitSmear smear = new BitSmear(smudge);
		//			smear.bind("Red Shift");
		//			smear.bind("Green Shift");
		//			smear.bind("Blue Shift");
		//			smear.bind("Red Mask");
		//			smear.bind("Green Mask");
		//			smear.bind("Blue Mask");
		//		
		//		SpectralShift spectral = new SpectralShift(smudge);
		//		spectral.getParameter("Colors").setInitial(60);
		//		spectral.getParameter("Sort").setInitial(true);
		//		spectral.bind("Colors");
		//		spectral.bind("Palette");
		//		spectral.bind("Sort");
		//		spectral.bind("Enable");
		//		spectral.getParameter("Reverse").setInitial(true);
		//		spectral.setBound(new EllipticalBound(1, 1));
		//		spectral.bind("Bound X");
		//		spectral.bind("Bound Y");
		//		spectral.bind("Bound Width");
		//		spectral.bind("Bound Height");
		//new AnimationControl(controller, spectral.getParameter("Shift"), .01);
		//
		//		
		//		ChromaShift shift = new ChromaShift(smudge);
		//		shift.bind("Layer 1 X");
		//		shift.bind("Layer 1 Y");
		//		shift.bind("Layer 2 X");
		//		shift.bind("Layer 2 Y");
		//		shift.bind("Layer 3 X");
		//		shift.bind("Layer 3 Y");
		//		shift.bind("X Offset");
		//		shift.bind("Y Offset");
		//		shift.bind("Height");
		//		shift.bind("Width");
		//		shift.bind("Bitwise Choice");
		//		shift.bind("Bit Shift");

		//new AnimationControl(controller, shift.getParameter("Amount"));

		//		PixelSort sort = new PixelSort(smudge);
		//		sort.setCoordFunction(new Converge;
		//		sort.getParameter("Threshold").setInitial(.1);
		//		sort.getParameter("Reverse").setInitial(true);
		//		sort.bind("Threshold");
		//		sort.bind("Reverse");
		//		sort.bind("Enable");
		//		sort.getParameter("Enable").setInitial(false);
		//		
		//		MonotonicMap map = new MonotonicMap(smudge);
		//		map.bind("Shift");

		//		PixelSort sort1 = new PixelSort(smudge);
		//		sort1.setCoordFunction(new ConvergeCoordFunction());
		//		sort1.bind("Threshold");
		//		sort1.bind("Reverse");
		//		sort1.bind("Enable");
		//		sort1.getParameter("Enable").setInitial(false);

		//		PixelSort sort2 = new PixelSort(smudge);
		//		sort2.setCoordFunction(new RowCoords());
		//		sort2.bind("Threshold");
		//		sort2.bind("Reverse");
		//		sort2.bind("Enable");
		//		sort2.getParameter("Enable").setInitial(false);
		//		

		//		PixelShift shift = new PixelShift(smudge);
		//		shift.setCoordFunction(new ConvergeCoordFunction());
		//		//shift.setBound(new EllipticalBound(1, 1));
		//		shift.getParameter("Intervals").setInitial(3);
		//		shift.bind("Intervals");
		//		shift.getParameter("Amount").setInitial(.2);
		//		shift.bind("Amount");
		//		shift.bind("Enable");
		//		shift.getParameter("Enable").setInitial(false);
		//		new AnimationControl(controller, shift.getParameter("Amount"));

		PixelShift shift1 = new PixelShift(smudge);
		//shift1.setBound(new EllipticalBound(1, 1));
		shift1.setCoordFunction(new ColumnCoords());
		//shift1.getParameter("Intervals").setInitial(3);
		shift1.bind("Intervals");
		shift1.getParameter("Amount").setInitial(.2);
		shift1.bind("Enable");
		shift1.getParameter("Enable").setInitial(false);
		//shift1.bind("Amount");

		//shift1.bind("Start");
		//shift1.bind("End");

		new AnimateByStepControl(controller, shift1.getParameter("Amount"));
		//new AnimationControl(controller, shift1.getParameter("End"));
		//new AnimationControl(controller, shift1.getParameter("Start"));

		new DownsampleControl(controller, 1);

		// Declare your view
		new JView(controller);

		controller.bindDevice("Arturia BeatStep");
		controller.start();
	}
}
