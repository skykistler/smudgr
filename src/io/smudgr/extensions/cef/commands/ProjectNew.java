package io.smudgr.extensions.cef.commands;

import io.smudgr.app.Controller;
import io.smudgr.extensions.cef.util.CefMessage;
import io.smudgr.project.ProjectLoader;

public class ProjectNew implements CefCommand {

	public String getCommand() {
		return "project.new";
	}

	public CefMessage execute(CefMessage data) {
		(new Thread() {
			public void run() {
				ProjectLoader project = new ProjectLoader();
				project.load();

				Controller.getInstance().start();
			}
		}).start();

		return null;
	}

}
