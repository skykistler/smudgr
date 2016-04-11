package io.smudgr.view;

import io.smudgr.project.ProjectIdManager.HasProjectId;
import io.smudgr.smudge.source.Frame;

public interface View extends HasProjectId {
	public void start();

	public void update(Frame frame);

	public void stop();

}
