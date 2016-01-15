package io.smudgr;

import io.smudgr.alg.ChromaShift;
import io.smudgr.alg.PixelShift;
import io.smudgr.alg.PixelSort;
import io.smudgr.alg.SpectralShift;
import io.smudgr.alg.coord.RowCoords;
import io.smudgr.controller.controls.AnimationControl;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.controls.VideoControl;
import io.smudgr.controller.device.MidiController;
import io.smudgr.view.JView;

public class EricMain {
	public static void main(String[] args) {
		// Declare your controller
		MidiController controller = new MidiController(11);

		// Make a smudge
		Smudge smudge = new Smudge("test", "emma.jpg");
		//new VideoControl(controller, "acrosstheuniverse.mp4", 3000);

		// Set smudge before doing anything
		controller.setSmudge(smudge);

//		PixelSort sort = new PixelSort(smudge);
//		sort.setCoordFunction(new RowCoords());
//		sort.bind("Threshold");
//		sort.bind("Reverse");
//		sort.bind("Enable");

		ChromaShift shift = new ChromaShift(smudge);
		shift.bind("Layer 1 X");
		shift.bind("Layer 1 Y");
		shift.bind("Layer 2 X");
		shift.bind("Layer 2 Y");
		shift.bind("Layer 3 X");
		shift.bind("Layer 3 Y");
		shift.bind("Bound X");
		shift.bind("Bound Y");
		shift.bind("Bound Width");
		shift.bind("Bound Height");
		
		//new AnimationControl(controller, shift.getParameter("Amount"));

//		SpectralShift spectral = new SpectralShift(smudge);
//		spectral.getParameter("Colors").setInitial(10);
//		spectral.getParameter("Sort").setInitial(true);
//		spectral.bind("Colors");
//		spectral.bind("Sort");
//		spectral.bind("Enable");
//		new AnimationControl(controller, spectral.getParameter("Shift"));

		new DownsampleControl(controller, 1);

		// Declare your view
		new JView(controller);

		controller.bindDevice("Arturia BeatStep Pro");
		controller.start();
	}
}
