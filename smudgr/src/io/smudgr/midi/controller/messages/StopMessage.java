package io.smudgr.midi.controller.messages;

import io.smudgr.controller.controls.Controllable;

public class StopMessage implements MidiMessageStrategy {

	@Override
	public void input(Controllable c, int value) {
		c.inputOff(0);
	}

}
