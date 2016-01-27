package io.smudgr.controller.device;

import io.smudgr.controller.controls.Controllable;

public class NoteOffStrategy implements MidiControlStrategy {

	public void input(Controllable c, int value) {
		c.inputOff(value);
	}

}
