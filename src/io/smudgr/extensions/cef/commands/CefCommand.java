package io.smudgr.extensions.cef.commands;

import io.smudgr.app.Controller;
import io.smudgr.extensions.cef.CefExtension;
import io.smudgr.extensions.cef.util.CefMessage;
import io.smudgr.project.Project;

public interface CefCommand {

	public String getCommand();

	public CefMessage execute(CefMessage data);

	public default void sendMessage(CefMessage message) {
		CefExtension cef = (CefExtension) getController().getExtension("CEF");

		cef.getServer().sendMessage(message);
	}

	public default Controller getController() {
		return Controller.getInstance();
	}

	public default Project getProject() {
		return getController().getProject();
	}

}
