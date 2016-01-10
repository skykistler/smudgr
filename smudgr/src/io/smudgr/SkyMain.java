package io.smudgr;

import io.smudgr.alg.PixelShift;
import io.smudgr.alg.PixelSort;
import io.smudgr.alg.SpectralShift;
import io.smudgr.controller.controls.AnimationControl;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.device.MidiController;
import io.smudgr.view.JView;

public class SkyMain {

	public static void main(String[] args) {
		// Declare your controller
		MidiController controller = new MidiController(11);

		// Make a smudge
		//		Smudge smudge = new Smudge("test", "planes statue of liberty.png");
		Smudge smudge = new Smudge("test", "lilly.png");

		// Set smudge before doing anything
		controller.setSmudge(smudge);

		PixelSort sort = new PixelSort(smudge);
		sort.bind("Threshold");
		sort.bind("Reverse");

		PixelShift shift = new PixelShift(smudge);
		shift.getParameter("Intervals").setInitial(70);
		shift.bind("Intervals");
		shift.getParameter("Amount").setInitial(.2);
		new AnimationControl(controller, shift.getParameter("Amount"));

		SpectralShift spectral = new SpectralShift(smudge);
		spectral.getParameter("Colors").setInitial(120);
		spectral.getParameter("Sort").setInitial(true);
		new AnimationControl(controller, spectral.getParameter("Shift"));

		new DownsampleControl(controller, 2);

		// Declare your view
		new JView(controller);

		controller.bindDevice("Arturia BeatStepPro");
		controller.start();
	}

}
