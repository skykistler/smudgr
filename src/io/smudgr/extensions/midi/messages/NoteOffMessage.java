package io.smudgr.extensions.midi.messages;

import io.smudgr.app.controller.Controllable;

public class NoteOffMessage implements MidiMessageStrategy {

	public void input(Controllable c, int value) {
		c.inputOff();
	}

}
