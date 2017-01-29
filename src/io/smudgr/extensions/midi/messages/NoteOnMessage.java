package io.smudgr.extensions.midi.messages;

import io.smudgr.app.controller.Controllable;
import io.smudgr.engine.param.NumberParameter;

/**
 * Represents behavior for an {@link NoteOnMessage}
 */
public class NoteOnMessage implements MidiMessageStrategy {

	@Override
	public void input(Controllable c, int value) {
		if (c instanceof NumberParameter) {
			NumberParameter param = (NumberParameter) c;
			param.inputValue(value);
		} else {
			if (value == 0)
				c.inputOff();
			else
				c.inputOn();
		}
	}

}
