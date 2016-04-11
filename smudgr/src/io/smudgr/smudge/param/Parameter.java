package io.smudgr.smudge.param;

import io.smudgr.app.controls.Controllable;

public abstract class Parameter extends Controllable {

	public String getName() {
		return name;
	}

	private String name;
	private Parametric parent;
	protected boolean reverse;
	protected boolean continuous;

	public Parameter(String name, Parametric parent) {
		this.name = name;
		this.parent = parent;
		this.parent.addParameter(this);
	}

	public abstract void setInitial(Object o);

	public abstract void setValue(Object o);

	public abstract String getStringValue();

	public void setReverse(boolean rev) {
		reverse = rev;
	}

	public void setContinuous(boolean cont) {
		continuous = cont;
	}

	public String toString() {
		return getName();
	}

	public Parametric getParent() {
		return parent;
	}

}
