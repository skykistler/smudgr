package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;

/**
 * Get the current listening port of the Canvas streaming WebSocket.
 */
public class CanvasPort implements ApiCommand {

	@Override
	public String getCommand() {
		return "canvas.port";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		ApiMessage response = new ApiMessage();
		response.put("port", "8887");

		return response;
	}

}
