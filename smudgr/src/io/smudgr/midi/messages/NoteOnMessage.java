package io.smudgr.midi.messages;

import io.smudgr.controller.controls.Controllable;

public class NoteOnMessage implements MidiMessageStrategy {

	public void input(Controllable c, int value) {
		if (value == 0)
			c.inputOff(value);
		else
			c.inputOn(value);
	}

}
