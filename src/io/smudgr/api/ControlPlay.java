package io.smudgr.api;

public class ControlPlay implements ApiCommand {

	public String getCommand() {
		return "control.play";
	}

	public ApiMessage execute(ApiMessage data) {
		getController().start();

		return null;
	}

}
