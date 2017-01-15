package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;

/**
 * Continue execution of the current application instance.
 */
public class ControlPlay implements ApiCommand {

	@Override
	public String getCommand() {
		return "control.play";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		getController().start();

		return null;
	}

}
