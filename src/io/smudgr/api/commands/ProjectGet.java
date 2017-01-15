package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.util.PropertyMap;

/**
 * Get the project model as a serialized nested JSON object.
 */
public class ProjectGet implements ApiCommand {

	@Override
	public String getCommand() {
		return "project.get";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		PropertyMap project = new PropertyMap("project");
		getProject().save(project);

		ApiMessage projectDOM = ApiMessage.normalize(project);

		return ApiMessage.ok("project", projectDOM);
	}

}
