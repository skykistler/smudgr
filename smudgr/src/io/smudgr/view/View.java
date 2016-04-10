package io.smudgr.view;

import io.smudgr.app.ProjectIdManager.HasProjectId;

public interface View extends HasProjectId {
	public void start();

	public void update();

	public void stop();

}
