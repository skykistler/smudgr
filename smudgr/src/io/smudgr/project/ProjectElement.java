package io.smudgr.project;

import io.smudgr.app.Controller;

public interface ProjectElement {

	public default Project getProject() {
		return Controller.getInstance().getProject();
	}

	public default void save(PropertyMap pm) {
		pm.setAttribute("id", getProject().getIdProvider().getId(this));
	}

	public default void load(PropertyMap pm) {
		getProject().getIdProvider().put(this, Integer.parseInt(pm.getAttribute("id")));
	}

}
