package io.smudgr.app.output;

import io.smudgr.project.smudge.source.Frame;

public interface FrameOutput {

	public int getTargetFPS();

	public void open();

	public void addFrame(Frame f);

	public void close();

}
