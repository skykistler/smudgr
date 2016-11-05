package io.smudgr.app;

import io.smudgr.project.ProjectItem;

public interface Controllable extends ProjectItem {
	
	public static final String PROPERTY_MAP_KEY = "control";

	public String getName();

	public void inputValue(int value);

	public void inputOn();

	public void inputOff();

	public void increment();

	public void decrement();

}
