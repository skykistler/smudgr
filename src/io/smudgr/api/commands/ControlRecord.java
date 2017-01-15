package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.util.output.GifOutput;

public class ControlRecord implements ApiCommand {

	public String getCommand() {
		return "control.record";
	}

	public ApiMessage execute(ApiMessage data) {
		if (!getController().isOutputting()) {
			getController().startOutput(new GifOutput("record"));
		} else {
			getController().stopOutput();
		}

		return null;
	}

}
