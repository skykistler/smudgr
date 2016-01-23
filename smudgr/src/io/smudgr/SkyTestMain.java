package io.smudgr;

import io.smudgr.alg.ChannelCrush;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.device.MidiController;
import io.smudgr.view.JView;

public class SkyTestMain {
	public static void main(String[] args) {
		// Declare your controller
		MidiController controller = new MidiController(11);

		// Make a smudge
		Smudge smudge = new Smudge("test", "nicole.jpg");

		// Set smudge before doing anything
		controller.setSmudge(smudge);

		ChannelCrush smear = new ChannelCrush(smudge);
		smear.getParameter("Green Shift").setInitial(7);
		smear.getParameter("Blue Shift").setInitial(7);
		smear.getParameter("Red Mask").setInitial(0);
		smear.bind("Enable");

		new DownsampleControl(controller, 1);

		// Declare your view
		new JView(controller);

		controller.bindDevice("Arturia BeatStepPro");
		controller.start();
	}
}
