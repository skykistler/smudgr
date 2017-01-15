package io.smudgr.app.view;

import java.awt.Rectangle;

import io.smudgr.app.threads.ViewThread;
import io.smudgr.util.Frame;

public class MonitorView implements View {

	public String getName() {
		return "monitor window";
	}

	private Window window;

	public void start(ViewThread thread) {
		window = new Window();
		window.setBounds(new Rectangle(800, 600));
	}

	public void update(Frame frame) {
		window.update(frame);
	}

	public void stop() {
		window.stop();
	}

}
