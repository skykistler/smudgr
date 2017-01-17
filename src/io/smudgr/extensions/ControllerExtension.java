package io.smudgr.extensions;

import io.smudgr.api.ApiMessage;
import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.Project;
import io.smudgr.app.project.util.PropertyMap;

public interface ControllerExtension {

	public static final String PROJECT_MAP_TAG = "extension";

	public String getName();

	public void init();

	public void update();

	public void stop();

	public void sendMessage(ApiMessage message);

	public void save(PropertyMap pm);

	public void load(PropertyMap pm);

	public default Project getProject() {
		return Controller.getInstance().getProject();
	}

}
