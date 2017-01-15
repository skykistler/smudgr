package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;

/**
 * Pause execution of the current application instance.
 * 
 * @see ControlPlay
 */
public class ControlPause implements ApiCommand {

	@Override
	public String getCommand() {
		return "control.pause";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		getController().pause();

		return null;
	}

}
