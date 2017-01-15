package io.smudgr.project;

import io.smudgr.app.Controller;
import io.smudgr.project.util.PropertyMap;

public interface ProjectItem {

	public default Project getProject() {
		return Controller.getInstance().getProject();
	}

	public default void save(PropertyMap pm) {
		pm.setAttribute("id", getProject().getId(this));
	}

	public default void load(PropertyMap pm) {
		getProject().put(this, Integer.parseInt(pm.getAttribute("id")));
	}

}
