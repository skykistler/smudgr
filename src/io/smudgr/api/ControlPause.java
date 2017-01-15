package io.smudgr.api;

public class ControlPause implements ApiCommand {

	public String getCommand() {
		return "control.pause";
	}

	public ApiMessage execute(ApiMessage data) {
		getController().pause();

		return null;
	}

}
