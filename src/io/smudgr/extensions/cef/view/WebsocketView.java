package io.smudgr.extensions.cef.view;

import java.io.IOException;
import java.net.UnknownHostException;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.view.View;
import io.smudgr.util.Frame;

/**
 * The {@link WebsocketView} implementation of {@link View} streams frames
 * through a WebSocket to an arbitrary number of connected clients.
 */
public class WebsocketView implements View {

	@Override
	public String getName() {
		return "WebSocket server";
	}

	private FrameServer server;

	@Override
	public void start() {
		try {
			server = new FrameServer(8887);
			server.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
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
			server.stop();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
