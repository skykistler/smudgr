package io.smudgr.api;

import io.smudgr.app.Controller;
import io.smudgr.project.Project;

public interface ApiCommand {

	public String getCommand();

	public ApiMessage execute(ApiMessage data);

	public default void sendMessage(ApiMessage message) {
		getController().sendMessage(message);
	}

	public default Controller getController() {
		return Controller.getInstance();
	}

	public default Project getProject() {
		return getController().getProject();
	}

}
