package io.smudgr.util.source;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.SourceLibrary;
import io.smudgr.util.Frame;

/**
 * The {@link Source} interface defines methods for interacting with a
 * {@link Frame} input source.
 */
public interface Source {
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
	 * Gets the current application instance {@link SourceLibrary}
	 *
	 * @return {@link SourceLibrary}
	 */
	public default SourceLibrary getSourceLibrary() {
		return Controller.getInstance().getProject().getSourceLibrary();
	}

}
