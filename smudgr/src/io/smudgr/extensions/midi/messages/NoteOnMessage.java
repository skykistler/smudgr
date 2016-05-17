package io.smudgr.extensions.midi.messages;

import io.smudgr.app.Controllable;
import io.smudgr.project.smudge.param.NumberParameter;

public class NoteOnMessage implements MidiMessageStrategy {

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
