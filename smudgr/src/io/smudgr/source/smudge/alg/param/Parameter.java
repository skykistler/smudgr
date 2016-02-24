package io.smudgr.source.smudge.alg.param;

import io.smudgr.controller.controls.Controllable;
import io.smudgr.source.smudge.alg.Algorithm;

public abstract class Parameter extends Controllable {
	private Algorithm alg;
	protected boolean reverse;
	protected boolean continuous;

	public Parameter(Algorithm parent, String name) {
		super(parent.getParent().getController(), name);
		alg = parent;

		alg.addParameter(this);
	}

	public Algorithm getParent() {
		return alg;
	}

	public abstract void setInitial(Object o);

	public abstract void setValue(Object o);

	public void setReverse(boolean rev) {
		reverse = rev;
	}

	public void setContinuous(boolean cont) {
		continuous = cont;
	}

	public String toString() {
		return alg + " - " + getName();
	}

}
