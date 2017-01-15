package io.smudgr.extensions.midi.messages;

import io.smudgr.app.controller.Controllable;

public class ControlChangeMessage implements MidiMessageStrategy {

	public void input(Controllable c, int value) {
		this.input(c, value, false);
	}

	public void input(Controllable c, int value, boolean absolute) {
		if (absolute) {
			c.inputValue(value);
		} else {
			if (value < 64)
				for (int i = 0; i < 64 - value; i++)
					c.decrement();

			if (value > 64)
				for (int i = 0; i < value - 64; i++)
					c.increment();
		}
	}

}
