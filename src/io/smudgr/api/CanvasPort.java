package io.smudgr.api;

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
