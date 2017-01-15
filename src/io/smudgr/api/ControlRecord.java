package io.smudgr.api;

import io.smudgr.app.output.GifOutput;

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
