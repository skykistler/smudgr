package io.smudgr.extensions.cef.commands;

import io.smudgr.extensions.cef.util.CefMessage;

public class SmudgrPort implements CefCommand {

	public String getCommand() {
		return "smudgr.port";
	}

	public CefMessage execute(CefMessage data) {
		CefMessage response = new CefMessage();
		response.put("port", "45455");

		return response;
	}

}
