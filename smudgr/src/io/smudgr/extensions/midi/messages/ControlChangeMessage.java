package io.smudgr.extensions.midi.messages;

import io.smudgr.app.Controllable;

public class ControlChangeMessage implements MidiMessageStrategy {

	public void input(Controllable c, int value) {
		if (value < 64)
			for (int i = 0; i < 64 - value; i++)
				c.decrement();

		if (value > 64)
			for (int i = 0; i < value - 64; i++)
				c.increment();

		//		c.inputValue(value);
	}

}
