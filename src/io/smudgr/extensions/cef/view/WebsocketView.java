package io.smudgr.extensions.cef.view;

import java.io.IOException;
import java.net.UnknownHostException;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.threads.ViewThread;
import io.smudgr.app.view.View;
import io.smudgr.util.Frame;

public class WebsocketView implements View {

	public String getName() {
		return "WebSocket server";
	}

	private FrameServer server;

	public void start(ViewThread thread) {
		thread.setTarget(60);

		try {
			server = new FrameServer(8887);
			server.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void update(Frame frame) {
		if (Controller.getInstance().isPaused())
			return;

		server.setFrame(frame);
		server.writeFrame();
	}

	public void stop() {
		try {
			server.stop();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
