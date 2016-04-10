package io.smudgr.ext;

import io.smudgr.app.ProjectIdManager.HasProjectId;

public interface ControllerExtension extends HasProjectId {

	public String getName();

	public void init();

	public void update();

	public void stop();

}
