package io.smudgr.util.source;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.ProjectItem;
import io.smudgr.app.project.SourceLibrary;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.util.Frame;

/**
 * The {@link Source} interface defines methods for interacting with a
 * {@link Frame} input source.
 */
public interface Source extends ProjectItem {

	@Override
	public default String getTypeCategoryIdentifier() {
		return "source";
	}

	@Override
	public default String getTypeCategoryName() {
		return "Source";
	}

	/**
	 * Initialize the {@link Source}
	 */
	public void init();

	/**
	 * Update the source in time with the application thread.
	 * <p>
	 * Advance timed frames using this method.
	 */
	public void update();

	/**
	 * Gets the current {@link Frame} of this {@link Source}
	 *
	 * @return {@link Frame}
	 */
	public Frame getFrame();

	/**
	 * Dispose of this {@link Source}, its frames, and any allocated resources.
	 */
	public void dispose();

	/**
	 * Gets a minified preview of this {@link Source}, whether or not this
	 * source is currently disposed or not.
	 * 
	 * @return {@link Frame}
	 */
	public Frame getThumbnail();

	/**
	 * Gets user-identifying name of this {@link Source} instance.
	 * 
	 * @return {@link String}
	 */
	public String getName();

	/**
	 * Gets the current application instance {@link SourceLibrary}
	 *
	 * @return {@link SourceLibrary}
	 */
	public default SourceLibrary getSourceLibrary() {
		return Controller.getInstance().getProject().getSourceLibrary();
	}

	@Override
	public default void save(PropertyMap pm) {
		ProjectItem.super.save(pm);
	}

	@Override
	public default void load(PropertyMap pm) {
		ProjectItem.super.load(pm);
	}

}
