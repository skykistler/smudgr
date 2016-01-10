package io.smudgr.controller.device;

import io.smudgr.controller.controls.Controllable;

public class KnobControl implements MidiControlStrategy {

	public void input(Controllable c, int value) {
		if (value < 64)
			c.decrement();
		if (value > 64)
			c.increment();
	}

}
