package io.smudgr.app.view;

import io.smudgr.project.util.Frame;

public interface View {

	public String getName();

	public void start();

	public void update(Frame frame);

	public void stop();

}
