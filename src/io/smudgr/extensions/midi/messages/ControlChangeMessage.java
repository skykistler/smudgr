package io.smudgr.extensions.midi.messages;

import io.smudgr.app.controller.Controllable;

/**
 * Represents behavior for a CC {@link ControlChangeMessage}
 */
public class ControlChangeMessage implements MidiMessageStrategy {

	@Override
	public void input(Controllable c, int value) {
		this.input(c, value, false);
	}

	/**
	 * Alternative method when {@code absolute} setting is known.
	 *
	 * @param c
	 *            {@link Controllable}
	 * @param value
	 *            Value of CC message
	 * @param absolute
	 *            if {@code true} use {@link Controllable#inputValue(int)
	 *            Controllable.inputValue(value)}, otherwise use
	 *            {@link Controllable#increment()} or
	 *            {@link Controllable#decrement()}
	 */
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
