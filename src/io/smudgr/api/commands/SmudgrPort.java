package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.api.ApiServer;

/**
 * Get the current listening port of the smudgr WebSocket API
 */
public class SmudgrPort implements ApiCommand {

	@Override
	public String getCommand() {
		return "smudgr.port";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		ApiMessage response = new ApiMessage();
		response.put("port", ApiServer.API_PORT);

		return response;
	}

}
