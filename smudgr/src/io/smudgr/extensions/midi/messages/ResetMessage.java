package io.smudgr.extensions.midi.messages;

import io.smudgr.app.controls.Controllable;

public class ResetMessage implements MidiMessageStrategy {

	@Override
	public void input(Controllable c, int value) {
		c.inputOff(1);
	}

}
