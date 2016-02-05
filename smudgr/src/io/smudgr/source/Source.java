package io.smudgr.source;

public interface Source {
	public void init();

	public void update();

	public Frame getFrame();

}
