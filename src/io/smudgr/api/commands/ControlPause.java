package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;

public class ControlPause implements ApiCommand {

	public String getCommand() {
		return "control.pause";
	}

	public ApiMessage execute(ApiMessage data) {
		getController().pause();

		return null;
	}

}
