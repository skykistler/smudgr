package io.smudgr.ext;

import io.smudgr.controller.ProjectIdManager;
import io.smudgr.controller.ProjectIdManager.HasProjectId;

public interface ControllerExtension extends HasProjectId {

	public String getName();

	public void init();

	public void update();

	public void stop();

}
