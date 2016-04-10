package io.smudgr.smudge.source;

public interface Source {
	public void init();

	public void update();

	public Frame getFrame();

	public void dispose();

}