package io.smudgr.app.view;

import io.smudgr.api.PixelFrameServer;
import io.smudgr.app.controller.Controller;
import io.smudgr.util.PixelFrame;

/**
 * The {@link WebsocketView} implementation of {@link View} streams frames
 * through a WebSocket to an arbitrary number of connected clients.
 */
public class WebsocketView implements View {

	@Override
	public String getName() {
		return "WebSocket Frame Server";
	}

	private PixelFrameServer server;

	@Override
	public void start() {
		server = new PixelFrameServer(8887);
		server.start();
	}

	@Override
	public void update(PixelFrame frame) {
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
