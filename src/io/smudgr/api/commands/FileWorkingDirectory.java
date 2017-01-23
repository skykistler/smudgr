package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;

/**
 * Gets the current working directory of the smudgr process.
 */
public class FileWorkingDirectory implements ApiCommand {

	@Override
	public String getCommand() {
		return "file.workingDirectory";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		ApiMessage response = new ApiMessage();
		response.put("path", System.getProperty("user.dir"));

		return ApiMessage.success("file.workingDirectory", response);
	}

}
