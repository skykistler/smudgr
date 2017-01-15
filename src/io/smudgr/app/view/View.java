package io.smudgr.app.view;

import io.smudgr.app.threads.ViewThread;
import io.smudgr.util.Frame;

public interface View {

	public String getName();

	public void start(ViewThread thread);

	public void update(Frame frame);

	public void stop();

}
