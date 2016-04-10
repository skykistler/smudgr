package io.smudgr.view;

import io.smudgr.controller.ProjectIdManager.HasProjectId;

public interface View extends HasProjectId {
	public void start();

	public void update();

	public void stop();

}
