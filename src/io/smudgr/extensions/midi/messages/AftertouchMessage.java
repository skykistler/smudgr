package io.smudgr.extensions.midi.messages;

import io.smudgr.app.Controllable;

public class AftertouchMessage implements MidiMessageStrategy {

	public void input(Controllable c, int value) {
		c.inputValue(value);
	}

}
