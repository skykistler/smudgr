package io.smudgr.ext.midi.messages;

import io.smudgr.controller.controls.Controllable;

public class KnobMessage implements MidiMessageStrategy {

	public void input(Controllable c, int value) {
		if (value < 64)
			for (int i = 0; i < 64 - value; i++)
				c.decrement();

		if (value > 64)
			for (int i = 0; i < value - 64; i++)
				c.increment();
	}

}