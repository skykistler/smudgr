package io.smudgr.extensions;

import io.smudgr.project.PropertyMap;

public interface ControllerExtension {

	public String getName();

	public void init();

	public void update();

	public void stop();

	public void save(PropertyMap pm);

	public void load(PropertyMap pm);

}
