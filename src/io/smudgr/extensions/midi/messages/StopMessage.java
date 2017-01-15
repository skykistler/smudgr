package io.smudgr.extensions.midi.messages;

import io.smudgr.app.controller.Controllable;
import io.smudgr.app.controller.Controller;

public class StopMessage implements MidiMessageStrategy {

	public void input(Controllable c, int value) {
		Controller.getInstance().pause();
	}

}
