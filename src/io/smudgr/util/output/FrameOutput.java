package io.smudgr.util.output;

import io.smudgr.util.Frame;

public interface FrameOutput {

	public int getTargetFPS();

	public void open();

	public void addFrame(Frame f);

	public void close();

}
