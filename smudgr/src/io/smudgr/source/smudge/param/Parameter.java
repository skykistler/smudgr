package io.smudgr.source.smudge.param;

import io.smudgr.controller.controls.Controllable;

public abstract class Parameter extends Controllable {

	private Parametric parent;
	protected boolean reverse;
	protected boolean continuous;

	public Parameter(String name, Parametric parent) {
		super(name);

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
