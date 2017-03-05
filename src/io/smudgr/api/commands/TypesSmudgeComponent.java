package io.smudgr.api.commands;

import java.util.Set;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.Project;
import io.smudgr.engine.SmudgeComponent;

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
		return "types.smudge.component";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		if (data == null || !data.hasKey(SMUDGE_TYPE_ID_KEY)) {
			return ApiMessage.failed(getCommand(), new ApiMessage("message", "Must include 'smudge-type' of desired smudge components."));
		}

		String smudgeTypeId = data.get(SMUDGE_TYPE_ID_KEY);

		Set<String> typeIds = getProject().getSmudgeComponentLibrary().getIdList(smudgeTypeId);

		ApiMessage types = new ApiMessage();

		for (String id : typeIds) {
			SmudgeComponent component = getProject().getSmudgeComponentLibrary().getNewInstance(smudgeTypeId, id);

			ApiMessage component_data = new ApiMessage();
			component_data.put("id", id);
			component_data.put("name", component.getTypeName());
			component_data.put("type", component.getComponentTypeIdentifier());

			types.put(id, component_data);
		}

		return ApiMessage.ok(getCommand(), types);
	}

}
