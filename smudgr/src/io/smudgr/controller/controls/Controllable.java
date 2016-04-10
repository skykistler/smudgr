package io.smudgr.controller.controls;

import io.smudgr.controller.ProjectIdManager.HasProjectId;
import io.smudgr.controller.PropertyMap;

public abstract class Controllable implements HasProjectId {

	private boolean bindRequested = false;

	protected PropertyMap propertyMap = new PropertyMap();

	public abstract String getName();

	public void init() {

	}

	public void update() {

	}

	public void inputValue(int value) {

	}

	public void inputOn(int value) {

	}

	public void inputOff(int value) {

	}

	public void increment() {

	}

	public void decrement() {

	}

	public PropertyMap getPropertyMap() {
		return propertyMap;
	}

	public void savePropertyMap() {

	}

	public void loadPropertyMap() {

	}

	public void requestBind() {
		bindRequested = true;
	}

	public boolean isBindRequested() {
		return bindRequested;
	}

	public String toString() {
		return getName();
	}
}
