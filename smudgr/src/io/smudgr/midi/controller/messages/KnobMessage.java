package io.smudgr.midi.controller.messages;

import io.smudgr.controller.controls.Controllable;

public class KnobMessage implements MidiMessageStrategy {

	public void input(Controllable c, int value) {
		if (value < 64)
			c.decrement();
		if (value > 64)
			c.increment();
	}

}
