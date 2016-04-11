package io.smudgr.extensions.midi.messages;

import io.smudgr.app.controls.Controllable;

public class StartMessage implements MidiMessageStrategy {

	@Override
	public void input(Controllable c, int value) {
		c.inputOn(0);
	}

}
