package io.smudgr.app.view;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.threads.ViewThread;
import io.smudgr.engine.Smudge;
import io.smudgr.util.Frame;

/**
 * The {@link View} interface defines methods for passing frames to a viewport
 * at a configurable rate (frames per second).
 * <p>
 * {@link View} instances can be added to the current application instance using
 * {@link Controller#add(Object)}
 *
 * @see Controller#add(Object)
 */
public interface View {

	/**
	 * Gets the identifiable name of this {@link View}
	 *
	 * @return {@link String} identifier
	 */
	public String getName();

	/**
	 * Gets the ideal frames per second that this {@link View} should update at.
	 * Implementations of this method may return a constant or changing value.
	 * <p>
	 * The {@link ViewThread} for this {@link View} attempts to pass frames from
	 * the current application instance {@link Smudge} at this rate.
	 * <p>
	 * If the performance of the {@link Smudge} falls below this FPS, the
	 * {@link ViewThread} will still attempt to meet this rate, and may pass the
	 * same frame multiple times.
	 * <p>
	 * The default rate is {@link Controller#TARGET_FPS}.
	 *
	 * @return frames per second
	 */
	public default int getTargetFPS() {
		return Controller.TARGET_FPS;
	}

	public void start();

	public void update(Frame frame);

	public void stop();

}
