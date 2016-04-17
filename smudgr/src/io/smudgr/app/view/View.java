package io.smudgr.app.view;

import io.smudgr.project.smudge.source.Frame;

public interface View {
	public void start();

	public void update(Frame frame);

	public void stop();

}
