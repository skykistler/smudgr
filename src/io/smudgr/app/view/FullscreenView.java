package io.smudgr.app.view;

import io.smudgr.app.threads.ViewThread;
import io.smudgr.project.util.Frame;

public class FullscreenView implements View {

	public String getName() {
		return "fullscreen window";
	}

	private Window window;
	private int displayNumber;

	public FullscreenView(int displayNumber) {
		this.displayNumber = displayNumber;
	}

	public void start(ViewThread thread) {
		window = new Window(displayNumber);
	}

	public void update(Frame frame) {
		window.update(frame);
	}

	public void stop() {
		window.stop();
	}

}
