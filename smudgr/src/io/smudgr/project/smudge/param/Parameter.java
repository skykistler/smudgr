package io.smudgr.project.smudge.param;

import io.smudgr.app.Controllable;
import io.smudgr.project.ProjectElement;
import io.smudgr.project.PropertyMap;

public abstract class Parameter implements Controllable, ProjectElement {

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

	public abstract void setValue(Object o);

	public abstract String getStringValue();

	public void setReverse(boolean rev) {
		reverse = rev;
	}

	public void setContinuous(boolean cont) {
		continuous = cont;
	}

	public void save(PropertyMap pm) {
		pm.setAttribute("value", getStringValue());
	}

	public void load(PropertyMap pm) {
		if (pm.hasAttribute("value"))
			setValue(pm.getAttribute("value"));
	}

	public String toString() {
		return getName();
	}

	public Parametric getParent() {
		return parent;
	}

}
