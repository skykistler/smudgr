package io.smudgr.extensions.cef.commands;

import io.smudgr.app.Controller;
import io.smudgr.extensions.cef.util.CefMessage;

public class ControlPause implements CefCommand {

	public String getCommand() {
		return "control.pause";
	}

	public CefMessage execute(CefMessage data) {
		Controller.getInstance().pause();
		return null;
	}

}
