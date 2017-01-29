package io.smudgr.extensions.midi.messages;

import io.smudgr.app.controller.Controllable;
import io.smudgr.app.controller.Controller;

/**
 * Represents behavior for an {@link StartMessage}
 */
public class StartMessage implements MidiMessageStrategy {

	@Override
	public void input(Controllable c, int value) {
		Controller.getInstance().start();
	}

}
