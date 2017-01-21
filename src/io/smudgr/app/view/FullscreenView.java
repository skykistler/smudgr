package io.smudgr.app.view;

import io.smudgr.util.Frame;

/**
 * The {@link FullscreenView} creates a natively fullscreen {@link Window} on
 * the specified monitor.
 */
public class FullscreenView implements View {

	@Override
	public String getName() {
		return "fullscreen window";
	}

	private Window window;
	private int displayNumber;

	/**
	 * Create a natively fullscreen window on the given display.
	 *
	 * @param displayNumber
	 *            Monitor to use.
	 */
	public FullscreenView(int displayNumber) {
		this.displayNumber = displayNumber;
	}

	@Override
	public void start() {
		window = new Window(displayNumber);
	}

	@Override
	public void update(Frame frame) {
		window.update(frame);
	}

	@Override
	public void stop() {
		window.stop();
	}

}
