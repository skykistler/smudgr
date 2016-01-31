package io.smudgr.controller.device.messages;

import io.smudgr.controller.controls.Controllable;

public class StopMessage implements MidiMessageStrategy {

	@Override
	public void input(Controllable c, int value) {
		c.inputOff(0);
	}

}
