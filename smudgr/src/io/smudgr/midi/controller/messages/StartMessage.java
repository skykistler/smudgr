package io.smudgr.midi.controller.messages;

import io.smudgr.controller.controls.Controllable;

public class StartMessage implements MidiMessageStrategy {

	@Override
	public void input(Controllable c, int value) {
		c.inputOn(0);
	}

}