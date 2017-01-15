package io.smudgr.extensions;

import io.smudgr.api.ApiMessage;
import io.smudgr.app.Controller;
import io.smudgr.project.Project;
import io.smudgr.project.PropertyMap;

public interface ControllerExtension {

	public static final String PROPERTY_MAP_KEY = "extension";

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
