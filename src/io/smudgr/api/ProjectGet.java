package io.smudgr.api;

import io.smudgr.project.util.PropertyMap;

public class ProjectGet implements ApiCommand {

	public String getCommand() {
		return "project.get";
	}

	public ApiMessage execute(ApiMessage data) {
		PropertyMap project = new PropertyMap("project");
		getProject().save(project);

		ApiMessage projectDOM = ApiMessage.normalize(project);

		return ApiMessage.command("project", projectDOM);
	}

}
