package io.smudgr.app.view;

import java.awt.Rectangle;

import io.smudgr.util.PixelFrame;

/**
 * The {@link MonitorView} is for creating non-fullscreen windows to 'monitor'
 * the application output. This is ideal for testing, or for a VJ who needs to
 * see real-time feedback from the application but can not immediately see the
 * fullscreen 'production' output.
 */
public class MonitorView implements View {

	@Override
	public String getName() {
		return "monitor window";
	}

	private Window window;

	@Override
	public void start() {
		window = new Window();
		window.setBounds(new Rectangle(800, 600));
	}

	@Override
	public void update(PixelFrame frame) {
		window.update(frame);
	}

	@Override
	public void stop() {
		window.stop();
	}

}
