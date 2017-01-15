package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;

public class ControlPlay implements ApiCommand {

	public String getCommand() {
		return "control.play";
	}

	public ApiMessage execute(ApiMessage data) {
		getController().start();

		return null;
	}

}
