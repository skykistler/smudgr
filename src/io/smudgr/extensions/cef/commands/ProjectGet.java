package io.smudgr.extensions.cef.commands;

import io.smudgr.extensions.cef.util.CefMessage;
import io.smudgr.project.PropertyMap;

public class ProjectGet implements CefCommand {

	public String getCommand() {
		return "project.get";
	}

	public CefMessage execute(CefMessage data) {
		PropertyMap project = new PropertyMap("project");
		getProject().save(project);

		CefMessage projectDOM = CefMessage.normalize(project);

		return CefMessage.command("project", projectDOM);
	}

}
