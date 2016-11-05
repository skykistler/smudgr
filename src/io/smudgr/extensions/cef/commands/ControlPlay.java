package io.smudgr.extensions.cef.commands;

import io.smudgr.app.Controller;
import io.smudgr.extensions.cef.util.CefMessage;

public class ControlPlay implements CefCommand {

	public String getCommand() {
		return "control.play";
	}

	public CefMessage execute(CefMessage data) {
		Controller.getInstance().start();
		return null;
	}

}
