package io.smudgr.controller.controls;

import io.smudgr.controller.Controller;

public abstract class Controllable {

	private int id;
	private Controller controller;
	private String name;
	private boolean bindRequested = false;
	private PropertyMap propertyMap = new PropertyMap();

	public Controllable(String name) {
		setName(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void init() {

	}

	public void update() {

	}

	public abstract void inputValue(int value);

	public abstract void inputOn(int value);

	public abstract void inputOff(int value);

	public abstract void increment();

	public abstract void decrement();

	public PropertyMap getPropertyMap() {
		return propertyMap;
	}

	public void setProperties() {

	}

	public void getProperties() {

	}

	public void requestBind() {
		bindRequested = true;
	}

	public boolean isBindRequested() {
		return bindRequested;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller c) {
		controller = c;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public String toString() {
		return getName();
	}
}
