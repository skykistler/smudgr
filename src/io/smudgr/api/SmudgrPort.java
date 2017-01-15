package io.smudgr.api;

public class SmudgrPort implements ApiCommand {

	public String getCommand() {
		return "smudgr.port";
	}

	public ApiMessage execute(ApiMessage data) {
		ApiMessage response = new ApiMessage();
		response.put("port", "45455");

		return response;
	}

}
