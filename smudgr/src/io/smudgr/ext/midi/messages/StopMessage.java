package io.smudgr.ext.midi.messages;

import io.smudgr.app.controls.Controllable;

public class StopMessage implements MidiMessageStrategy {

	@Override
	public void input(Controllable c, int value) {
		c.inputOff(0);
	}

}
