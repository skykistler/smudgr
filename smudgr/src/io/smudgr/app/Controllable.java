package io.smudgr.app;

import io.smudgr.project.ProjectElement;

public interface Controllable extends ProjectElement {

	public String getName();

	public void inputValue(int value);

	public void inputOn(int value);

	public void inputOff(int value);

	public void increment();

	public void decrement();

}
