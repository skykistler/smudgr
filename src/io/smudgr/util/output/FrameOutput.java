package io.smudgr.util.output;

import io.smudgr.app.controller.Controller;
import io.smudgr.util.PixelFrame;

/**
 * The {@link FrameOutput} interface defines methods for interacting with an
 * output stream of an arbitrary format.
 */
public interface FrameOutput {

	/**
	 * Gets the ideal FPS that this output stream should record at.
	 *
	 * @return FPS
	 */
	public default int getTargetFPS() {
		return Controller.TARGET_FPS;
	}

	/**
	 * Opens the output stream.
	 */
	public void open();

	/**
	 * Add a frame to the output stream.
	 *
	 * @param f
	 *            {@link PixelFrame}
	 */
	public void addFrame(PixelFrame f);

	/**
	 * Closes the output stream.
	 */
	public void close();

}
