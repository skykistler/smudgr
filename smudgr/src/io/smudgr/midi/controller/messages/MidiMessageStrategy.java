package io.smudgr.midi.controller.messages;

import io.smudgr.controller.controls.Controllable;

public interface MidiMessageStrategy {
	public void input(Controllable c, int value);
}
