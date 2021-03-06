package io.smudgr.app.project;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.reflect.ReflectableType;
import io.smudgr.app.project.util.PropertyMap;

/**
 * Interface for defining objects that can be stored in the project.
 */
public interface ProjectItem extends ReflectableType {

	/**
	 * Get the currently loaded {@link Project}
	 *
	 * @return {@link Project}
	 */
	public default Project getProject() {
		return Controller.getInstance().getProject();
	}

	/**
	 * Save this item to the given {@link PropertyMap}.
	 * <p>
	 * Implementers of this method <b>must</b> call the parent
	 * {@code ProjectItem.super.save()} method in order for this
	 * {@link ProjectItem} to persist correctly.
	 *
	 * @param pm
	 *            The property map to save to.
	 *
	 * @see ProjectItem#load(PropertyMap)
	 */
	public default void save(PropertyMap pm) {
		if (getProject().contains(this))
			pm.setAttribute(PropertyMap.PROJECT_ID_ATTR, getProject().getId(this));

		pm.setAttribute(PropertyMap.TYPE_ID_ATTR, getTypeIdentifier());
		pm.setAttribute(PropertyMap.NAME_ATTR, getTypeName());
	}

	/**
	 * Load this item from the given {@link PropertyMap}.
	 * <p>
	 * Implementers of this method <b>must</b> call the parent
	 * {@code ProjectItem.super.load(pm)} method in order for this
	 * {@link ProjectItem} to load correctly.
	 *
	 * @param pm
	 *            The property map to load from.
	 *
	 * @see ProjectItem#save(PropertyMap)
	 */
	public default void load(PropertyMap pm) {
		if (pm.hasAttribute(PropertyMap.PROJECT_ID_ATTR))
			getProject().put(this, Integer.parseInt(pm.getAttribute(PropertyMap.PROJECT_ID_ATTR)));
		else
			getProject().add(this);
	}

}
