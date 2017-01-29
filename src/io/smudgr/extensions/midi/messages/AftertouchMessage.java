package io.smudgr.extensions.midi.messages;

import io.smudgr.app.controller.Controllable;

/**
 * Represents behavior for an {@link AftertouchMessage}
 */
public class AftertouchMessage implements MidiMessageStrategy {

	@Override
	public void input(Controllable c, int value) {
		c.inputValue(value);
	}

}
