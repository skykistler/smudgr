package io.smudgr.app.controls;

import io.smudgr.project.PropertyMap;
import io.smudgr.project.ProjectIdManager.HasProjectId;

public abstract class Controllable implements HasProjectId {

	public abstract String getName();
	private boolean bindRequested = false;
	protected PropertyMap propertyMap = new PropertyMap();


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
