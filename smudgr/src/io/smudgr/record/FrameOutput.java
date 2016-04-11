package io.smudgr.record;

import io.smudgr.smudge.source.Frame;

public interface FrameOutput {
	public void open();

	public void addFrame(Frame f);

	public void close();

}
