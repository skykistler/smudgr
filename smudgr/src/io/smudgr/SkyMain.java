package io.smudgr;

import io.smudgr.alg.ChannelCrush;
import io.smudgr.alg.ChannelDrift;
import io.smudgr.controller.controls.DownsampleControl;
import io.smudgr.controller.device.MidiController;
import io.smudgr.view.JView;

public class SkyMain {
	public static void main(String[] args) {
		// Declare your controller
		MidiController controller = new MidiController(11);

		// Make a smudge
		Smudge smudge = new Smudge("test", "nicole.jpg");

		// Set smudge before doing anything
		controller.setSmudge(smudge);

		ChannelCrush smear = new ChannelCrush(smudge);
		smear.bind("Red Shift");
		smear.bind("Green Shift");
		smear.bind("Blue Shift");
		smear.bind("Red Mask");
		smear.bind("Green Mask");
		smear.bind("Blue Mask");

		ChannelDrift drift = new ChannelDrift(smudge);
		drift.bind("Red Offset - X");
		drift.bind("Red Offset - Y");
		drift.bind("Green Offset - X");
		drift.bind("Green Offset - Y");
		drift.bind("Blue Offset - X");
		drift.bind("Blue Offset - Y");

		new DownsampleControl(controller, 1);

		// Declare your view
		new JView(controller);

		controller.bindDevice("Arturia BeatStepPro");
		controller.start();
	}
}
