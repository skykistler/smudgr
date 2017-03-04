package io.smudgr.app.view;

import io.smudgr.api.FrameServer;
import io.smudgr.app.controller.Controller;
import io.smudgr.util.Frame;

/**
 * The {@link WebsocketView} implementation of {@link View} streams frames
 * through a WebSocket to an arbitrary number of connected clients.
 */
public class WebsocketView implements View {

	@Override
	public String getName() {
		return "WebSocket frame server";
	}

	private FrameServer server;

	@Override
	public void start() {
		server = new FrameServer(8887);
		server.start();
	}

	@Override
	public void update(Frame frame) {
		if (Controller.getInstance().isPaused())
			return;

		server.setFrame(frame);
		server.writeFrame();
	}

	@Override
	public void stop() {
		try {
			server.stop(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getTargetFPS() {
		return 30;
	}

}
