package io.smudgr.controller.device.messages;

import io.smudgr.controller.controls.Controllable;

public class KnobStrategy implements MidiMessageStrategy {

	public void input(Controllable c, int value) {
		if (value < 64)
			c.decrement();
		if (value > 64)
			c.increment();
	}

}
