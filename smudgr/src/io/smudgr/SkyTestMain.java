package io.smudgr;

import io.smudgr.alg.Marbeler;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.device.MidiController;
import io.smudgr.view.JView;

public class SkyTestMain {
	public static void main(String[] args) {
		// Declare your controller
		MidiController controller = new MidiController(11, "test.map");

		// Make a smudge
		Smudge smudge = new Smudge("test", "nicole.png");

		// Set smudge before doing anything
		controller.setSmudge(smudge);

		Marbeler m = new Marbeler(smudge);
		m.bind("Offset - X/Y");
		m.bind("Frequency");
		m.bind("Iterations");
		m.bind("Strength");

		new DownsampleControl(controller, 1);

		// Declare your view
		new JView(controller);

		controller.bindDevice("Arturia BeatStep Pro");
		controller.start();
	}
}
