package io.smudgr.extensions.midi.messages;

import io.smudgr.app.controller.Controllable;
import io.smudgr.app.controller.Controller;

/**
 * Represents behavior for a {@link ContinueMessage}
 */
public class ContinueMessage implements MidiMessageStrategy {

	@Override
	public void input(Controllable c, int value) {
		Controller.getInstance().start();
	}

}
