package io.smudgr.controller.device.messages;

import io.smudgr.controller.controls.Controllable;

public class AftertouchStrategy implements MidiMessageStrategy {

	public void input(Controllable c, int value) {
		c.inputValue(value);
	}

}
