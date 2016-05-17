package io.smudgr.app.view;

import io.smudgr.project.smudge.util.Frame;

public class FullscreenView implements View {

	private Window window;
	private int displayNumber;

	public FullscreenView(int displayNumber) {
		this.displayNumber = displayNumber;
	}

	public void start() {
		window = new Window(displayNumber);
	}

	public void update(Frame frame) {
		window.update(frame);
	}

	public void stop() {
		window.stop();
	}

}
