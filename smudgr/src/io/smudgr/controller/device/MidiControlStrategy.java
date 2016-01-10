package io.smudgr.controller.device;

import io.smudgr.controller.controls.Controllable;

public interface MidiControlStrategy {
	public void input(Controllable c, int value);
}
