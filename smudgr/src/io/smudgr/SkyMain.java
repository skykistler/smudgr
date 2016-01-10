package io.smudgr;

import io.smudgr.alg.PixelSort;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.device.MidiController;
import io.smudgr.view.JView;

public class SkyMain {

	public static void main(String[] args) {
		// Declare your controller
		MidiController controller = new MidiController(11);

		// Make a smudge
		Smudge smudge = new Smudge("test", "galaxies.jpg");

		// Set smudge before doing anything
		controller.setSmudge(smudge);

		PixelSort sort = new PixelSort(smudge);
		sort.bind("Threshold");
		sort.bind("Reverse");

		// Declare your controls
		new DownsampleControl(controller, 1);

		// Declare your view
		new JView(controller);

		controller.bindDevice("Arturia BeatStep Pro");
		controller.start();
	}

}
