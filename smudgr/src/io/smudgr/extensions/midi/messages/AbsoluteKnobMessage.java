package io.smudgr.extensions.midi.messages;

import io.smudgr.app.Controllable;

public class AbsoluteKnobMessage implements MidiMessageStrategy {

	public void input(Controllable c, int value) {
		c.inputValue(value);
	}

}
