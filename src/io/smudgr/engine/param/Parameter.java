package io.smudgr.engine.param;

import io.smudgr.app.controller.Controllable;
import io.smudgr.app.project.ProjectItem;
import io.smudgr.app.project.util.PropertyMap;

public abstract class Parameter implements Controllable, ProjectItem {

	public static final String PROPERTY_MAP_KEY = "parameter";

	public abstract String getType();

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

	protected abstract void setValueFromObject(Object o);

	public void setValue(Object o) {
		setValue(o, null);
	}

	public void setValue(Object o, ParameterObserver ignoreObserver) {
		setValueFromObject(o);

		if (getProject() != null && getProject().getParameterObserverNotifier() != null)
			getProject().getParameterObserverNotifier().notify(this, ignoreObserver);
	}

	public abstract String getStringValue();

	public void setReverse(boolean rev) {
		reverse = rev;
	}

	public void setContinuous(boolean cont) {
		continuous = cont;
	}

	public void save(PropertyMap pm) {
		pm.setAttribute("type", getType());
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
