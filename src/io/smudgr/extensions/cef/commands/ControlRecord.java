package io.smudgr.extensions.cef.commands;

import io.smudgr.app.output.GifOutput;
import io.smudgr.extensions.cef.util.CefMessage;

public class ControlRecord implements CefCommand {

	public String getCommand() {
		return "control.record";
	}

	public CefMessage execute(CefMessage data) {
		if (!getController().isOutputting()) {
			getController().startOutput(new GifOutput("record"));
		} else {
			getController().stopOutput();
		}

		return null;
	}

}
