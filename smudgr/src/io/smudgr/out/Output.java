package io.smudgr.out;

import io.smudgr.source.Frame;

public interface Output {
	public void open();

	public void addFrame(Frame f);

	public void close();

}
