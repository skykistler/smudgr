package io.smudgr.controller.device.messages;

import io.smudgr.controller.controls.Controllable;

public interface MidiMessageStrategy {
	public void input(Controllable c, int value);
}
