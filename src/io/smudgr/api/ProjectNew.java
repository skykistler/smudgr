package io.smudgr.api;

import io.smudgr.project.util.ProjectLoader;

public class ProjectNew implements ApiCommand {

	public String getCommand() {
		return "project.new";
	}

	public ApiMessage execute(ApiMessage data) {
		(new Thread() {
			public void run() {
				ProjectLoader project = new ProjectLoader();
				project.load();

				getController().start();
			}
		}).start();

		return null;
	}

}
