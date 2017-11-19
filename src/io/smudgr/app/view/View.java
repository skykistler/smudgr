package io.smudgr.app.view;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.threads.ViewThread;
import io.smudgr.engine.Smudge;
import io.smudgr.util.PixelFrame;

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
	 * A {@link ViewThread} will attempt to pass frames to this {@link View}
	 * from the current {@link Smudge} at this rate.
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

	/**
	 * Implementations of the {@link View#start()} method should prepare the
	 * view's resources
	 */
	public void start();

	/**
	 * Implementations of the {@link View#update(PixelFrame)} method should update
	 * the view with the new frame.
	 *
	 * @param frame
	 *            Passed frame
	 */
	public void update(PixelFrame frame);

	/**
	 * Implementations of the {@link View#stop()} method should properly dispose
	 * of any open resources.
	 */
	public void stop();

}
