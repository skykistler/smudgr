package io.smudgr;

import io.smudgr.alg.PixelSort;
import io.smudgr.alg.SpectralShift;
import io.smudgr.controller.controls.AnimationControl;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.controls.VideoControl;
import io.smudgr.controller.device.MidiController;
import io.smudgr.view.JView;

public class SkyMain {

	public static void main(String[] args) {
		// Declare your controller
		MidiController controller = new MidiController(11);

		// Make a smudge
		Smudge smudge = new Smudge("test", "nicole.jpg");
		new VideoControl(controller, "acrosstheuniverse.mp4", 200);

		// Set smudge before doing anything
		controller.setSmudge(smudge);

		PixelSort sort = new PixelSort(smudge);
		sort.bind("Threshold");
		sort.bind("Reverse");

		//		PixelShift shift = new PixelShift(smudge);
		//		shift.getParameter("Intervals").setInitial(3);
		//		shift.bind("Intervals");
		//		shift.getParameter("Amount").setInitial(.2);
		//		new AnimationControl(controller, shift.getParameter("Amount"));

		SpectralShift spectral = new SpectralShift(smudge);
		spectral.getParameter("Colors").setInitial(50);
		spectral.getParameter("Sort").setInitial(true);
		spectral.bind("Colors");
		spectral.bind("Sort");
		new AnimationControl(controller, spectral.getParameter("Shift"));

		new DownsampleControl(controller, 1);

		// Declare your view
		new JView(controller);

		//		controller.bindDevice("Arturia BeatStepPro");
		controller.start();
	}

}
