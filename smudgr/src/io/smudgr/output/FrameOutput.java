package io.smudgr.output;

import io.smudgr.source.Frame;

public interface FrameOutput {
	public void open();

	public void addFrame(Frame f);

	public void close();

}