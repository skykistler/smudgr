package io.smudgr.app;

import io.smudgr.project.ProjectElement;

public interface Controllable extends ProjectElement {

	public String getName();

	public void inputValue(int value);

	public void inputOn();

	public void inputOff();

	public void increment();

	public void decrement();

}
