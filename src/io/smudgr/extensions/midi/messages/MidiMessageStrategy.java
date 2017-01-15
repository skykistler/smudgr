package io.smudgr.extensions.midi.messages;

import io.smudgr.app.controller.Controllable;

public interface MidiMessageStrategy {
	public void input(Controllable c, int value);
}
