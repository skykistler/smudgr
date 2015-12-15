package me.skykistler.smudgr.alg.param;

import me.skykistler.smudgr.alg.Algorithm;
import me.skykistler.smudgr.controller.controls.Controllable;

public abstract class Parameter extends Controllable {
	private Algorithm alg;
	protected boolean reverse;
	protected boolean continuous;

	public Parameter(Algorithm parent, String name) {
		super(name);
		alg = parent;

		alg.addParameter(this);
	}

	public Algorithm getParent() {
		return alg;
	}

	public abstract void setValue(Object o);

	public void setReverse(boolean rev) {
		reverse = rev;
	}

	public void setContinuous(boolean cont) {
		continuous = cont;
	}

}
