package io.smudgr;

import io.smudgr.alg.PixelShift;
import io.smudgr.alg.PixelSort;
import io.smudgr.alg.SpectralShift;
import io.smudgr.alg.coord.RowCoords;
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
		Smudge smudge = new Smudge("test", "lilly.png");
		new VideoControl(controller, "acrosstheuniverse.mp4", 3000);

		// Set smudge before doing anything
		controller.setSmudge(smudge);

		PixelSort sort = new PixelSort(smudge);
		sort.setCoordFunction(new RowCoords());
		sort.bind("Threshold");
		sort.bind("Reverse");
		sort.bind("Enable");

		PixelShift shift = new PixelShift(smudge);
		shift.getParameter("Intervals").setInitial(3);
		shift.bind("Intervals");
		shift.getParameter("Amount").setInitial(.2);
		shift.bind("Enable");
		new AnimationControl(controller, shift.getParameter("Amount"));

		SpectralShift spectral = new SpectralShift(smudge);
		spectral.getParameter("Colors").setInitial(10);
		spectral.getParameter("Sort").setInitial(true);
		spectral.bind("Colors");
		spectral.bind("Sort");
		spectral.bind("Enable");
		new AnimationControl(controller, spectral.getParameter("Shift"));

		new DownsampleControl(controller, 1);

		// Declare your view
		new JView(controller);

		//		controller.bindDevice("Arturia BeatStep Pro");
		controller.start();
	}

}
