package io.smudgr.extensions.cef.commands;

import io.smudgr.extensions.cef.util.CefMessage;

public class CanvasPort implements CefCommand {

	public String getCommand() {
		return "canvas.port";
	}

	public CefMessage execute(CefMessage data) {
		CefMessage response = new CefMessage();
		response.put("port", "8887");

		return response;
	}

}
