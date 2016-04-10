package io.smudgr.ext;

public interface ControllerExtension {

	public String getName();

	public void init();

	public void update();

	public void stop();

}
