package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;

public class CanvasPort implements ApiCommand {

	public String getCommand() {
		return "canvas.port";
	}

	public ApiMessage execute(ApiMessage data) {
		ApiMessage response = new ApiMessage();
		response.put("port", "8887");

		return response;
	}

}
