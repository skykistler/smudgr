package io.smudgr.controller.device;

import io.smudgr.controller.controls.Controllable;

public class NoteOnControl implements MidiControlStrategy {

	public void input(Controllable c, int value) {
		c.inputOn(value);
	}

}