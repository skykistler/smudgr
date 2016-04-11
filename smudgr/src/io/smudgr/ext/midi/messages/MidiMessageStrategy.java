package io.smudgr.ext.midi.messages;

import io.smudgr.app.controls.Controllable;

public interface MidiMessageStrategy {
	public void input(Controllable c, int value);
}
