package io.smudgr.ext.midi.messages;

import io.smudgr.app.controls.Controllable;

public class AftertouchMessage implements MidiMessageStrategy {

	public void input(Controllable c, int value) {
		c.inputValue(value);
	}

}
