package io.smudgr.extensions.cef.view;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import io.smudgr.project.smudge.source.Image;
import io.smudgr.project.smudge.util.Frame;

public class FrameServer extends WebSocketServer {

	private volatile Frame frame = null;
	private volatile boolean dimensionsChanged;

	private ByteBuffer buffer = null;

	public FrameServer() throws UnknownHostException {
		this(0);
	}

	public FrameServer(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}

	public void setFrame(Frame f) {
		if (frame == null || f.getWidth() != frame.getWidth() || f.getHeight() != frame.getHeight())
			dimensionsChanged = true;

		frame = f.copy();

		if (dimensionsChanged || buffer == null) {
			int bufferSize = frame.pixels.length * 4;
			buffer = ByteBuffer.allocate(bufferSize);
			buffer.order(ByteOrder.BIG_ENDIAN);
		}
	}

	public void onOpen(WebSocket arg0, ClientHandshake arg1) {
	}

	public void onMessage(WebSocket arg0, String arg1) {
		if (frame == null)
			return;

		Frame toDraw = frame.copy();

		if (dimensionsChanged) {
			arg0.send("w:" + toDraw.getWidth());
			arg0.send("h:" + toDraw.getHeight());
			dimensionsChanged = false;
		}

		buffer.position(0);

		for (int i = 0; i < toDraw.pixels.length; i++) {
			int color = (toDraw.pixels[i] << 8) | 0xFF;
			buffer.putInt(color);
		}

		buffer.flip();

		arg0.send(buffer);
	}

	public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
	}

	public void onError(WebSocket arg0, Exception arg1) {
	}

	public static void main(String[] args) throws UnknownHostException {
		FrameServer testServer = new FrameServer(8887);

		Image testImage = new Image("data/nicole.jpg");
		testImage.init();

		testServer.setFrame(testImage.getFrame());

		testServer.start();
	}

}
