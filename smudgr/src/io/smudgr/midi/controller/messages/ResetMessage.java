package io.smudgr.midi.controller.messages;

import io.smudgr.controller.controls.Controllable;

public class ResetMessage implements MidiMessageStrategy {

	@Override
	public void input(Controllable c, int value) {
		c.inputOff(1);
	}

}
