package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.util.ProjectLoader;

/**
 * Create a new project and load it to the current application instance.
 */
public class ProjectNew implements ApiCommand {

	@Override
	public String getCommand() {
		return "project.new";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		(new Thread() {
			@Override
			public void run() {
				ProjectLoader project = new ProjectLoader();
				project.load();

				getController().start();
			}
		}).start();

		return null;
	}

}
