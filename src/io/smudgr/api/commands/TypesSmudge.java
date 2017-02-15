package io.smudgr.api.commands;

import java.util.Set;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.Project;

/**
 * Gets available smudge types, as identifiers and display names
 *
 * @see Project#getSmudgeLibrary()
 */
public class TypesSmudge implements ApiCommand {

	@Override
	public String getCommand() {
		return "smudge.types";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		Set<String> typeIds = getProject().getSmudgeLibrary().getIdList();

		ApiMessage types = new ApiMessage();

		for (String id : typeIds)
			types.put(id, getProject().getSmudgeLibrary().getNameById(id));

		return ApiMessage.ok(getCommand(), types);
	}

}
