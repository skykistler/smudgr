package io.smudgr.extensions.cef.view;

import java.io.IOException;
import java.net.UnknownHostException;

import io.smudgr.app.threads.ViewThread;
import io.smudgr.app.view.View;
import io.smudgr.project.util.Frame;

public class WebsocketView implements View {

	public String getName() {
		return "WebSocket server";
	}

	private FrameServer server;

	public void start(ViewThread thread) {
		thread.setTarget(30);

		try {
			server = new FrameServer(8887);
			server.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void update(Frame frame) {
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
