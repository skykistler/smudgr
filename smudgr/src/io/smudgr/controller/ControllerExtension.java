package io.smudgr.controller;

public abstract class ControllerExtension {

	private Controller parent;

	public void init() {

	}

	public void update() {

	}

	public void stop() {

	}

	public void setParent(Controller parent) {
		this.parent = parent;
	}

	public Controller getParent() {
		return parent;
	}

}
