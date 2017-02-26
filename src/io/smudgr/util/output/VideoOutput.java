/**
 *
 */
package io.smudgr.util.output;

import io.smudgr.app.controller.Controller;
import io.smudgr.util.Frame;

/**
 * The {@link VideoOutput} class streams frames to a video file using FFMPEG.
 */
public class VideoOutput implements FrameOutput {

	private String path;

	/**
	 * Create a new {@link VideoOutput} with the given name
	 *
	 * @param name
	 *            {@link String}
	 */
	public VideoOutput(String name) {
		path = Controller.getInstance().getProject().getOutputPath() + name + "_" + System.currentTimeMillis() + ".mp4";
	}

	@Override
	public int getTargetFPS() {
		return 60;
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFrame(Frame f) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
