package io.smudgr.extensions.midi.messages;

import io.smudgr.app.Controllable;
import io.smudgr.app.Controller;

public class StopMessage implements MidiMessageStrategy {

	public void input(Controllable c, int value) {
		Controller.getInstance().pause();
	}

}
