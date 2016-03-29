package io.smudgr.ext.midi.messages;

import io.smudgr.controller.controls.Controllable;

public class ContinueMessage implements MidiMessageStrategy {

	@Override
	public void input(Controllable c, int value) {
		c.inputOn(1);
	}

}
