package io.smudgr.extensions.cef.commands;

import io.smudgr.extensions.cef.util.CefMessage;

public class ControlPause implements CefCommand {

	public String getCommand() {
		return "control.pause";
	}

	public CefMessage execute(CefMessage data) {
		getController().pause();

		return null;
	}

}
