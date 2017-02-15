package io.smudgr.api.commands;

import java.util.Set;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.Project;

/**
 * Gets available smudge component types for a particular smudge type, as
 * identifiers and display names
 *
 * @see Project#getSmudgeComponentLibrary()
 */
public class TypesSmudgeComponent implements ApiCommand {

	private static final String SMUDGE_TYPE_ID_KEY = "smudge-type";

	@Override
	public String getCommand() {
		return "smudge.component.types";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		if (!data.hasKey(SMUDGE_TYPE_ID_KEY)) {
			return ApiMessage.failed(getCommand(), new ApiMessage("message", "Must include 'smudge-type' of desired smudge components."));
		}

		String smudgeTypeId = data.get(SMUDGE_TYPE_ID_KEY);

		Set<String> typeIds = getProject().getSmudgeComponentLibrary().getIdList(smudgeTypeId);

		ApiMessage types = new ApiMessage();

		for (String id : typeIds)
			types.put(id, getProject().getSmudgeComponentLibrary().getNameById(smudgeTypeId, id));

		return ApiMessage.ok(getCommand(), types);
	}

}
