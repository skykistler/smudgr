package io.smudgr.extensions.cef.commands;

import io.smudgr.app.Controller;
import io.smudgr.project.ProjectLoader;

public class NewSmudge implements CefCommand {

	public String getCommand() {
		return "smudge.new";
	}

	public boolean request(String content) {
		(new Thread() {
			public void run() {
				ProjectLoader project = new ProjectLoader();
				project.load();

				Controller.getInstance().start();
			}
		}).start();

		return true;
	}

	public String onSuccess() {
		return "Starting new project";
	}

	public String onFailure() {
		return "Failed to start new project";
	}

}
