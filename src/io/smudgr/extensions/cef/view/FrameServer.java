package io.smudgr.extensions.cef.view;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import io.smudgr.project.util.Frame;

public class FrameServer extends WebSocketServer {

	private volatile Frame frame = null;
	private volatile boolean dimensionsChanged;
	private volatile ArrayList<WebSocket> activeSockets = new ArrayList<WebSocket>();

	public volatile boolean writing;

	private ByteBuffer buffer = null;
	private int bufferSize, i, color;

	public FrameServer() throws UnknownHostException {
		this(0);
	}

	public FrameServer(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}

	public void setFrame(Frame f) {
		Frame resized = f.fitToSize(600, 600, false);

		if (frame == null || resized.getWidth() != frame.getWidth() || resized.getHeight() != frame.getHeight())
			dimensionsChanged = true;

		if (frame != null)
			frame.dispose();

		frame = resized;

		bufferSize = frame.pixels.length * 4;
		if (buffer == null || buffer.capacity() != bufferSize) {
			buffer = ByteBuffer.allocate(bufferSize);
			buffer.order(ByteOrder.BIG_ENDIAN);
		}
	}

	public synchronized void writeFrame() {
		if (writing || frame == null || activeSockets.size() == 0)
			return;

		writing = true;

		Frame toDraw = frame.copy();

		if (dimensionsChanged) {
			updateSize();
			dimensionsChanged = false;
		}

		buffer.position(0);

		for (i = 0; i < toDraw.pixels.length; i++) {
			color = (toDraw.pixels[i] << 8) | 0xFF;
			buffer.putInt(color);
		}

		for (WebSocket ws : activeSockets) {
			if (ws.hasBufferedData())
				continue;

			buffer.flip();
			ws.send(buffer);
		}

		toDraw.dispose();

		writing = false;
	}

	private synchronized void updateSize() {
		for (WebSocket ws : activeSockets) {
			ws.send("w:" + frame.getWidth());
			ws.send("h:" + frame.getHeight());
		}
	}

	public synchronized void onOpen(WebSocket arg0, ClientHandshake arg1) {
		activeSockets.add(arg0);

		updateSize();
		writeFrame();
	}

	public void onMessage(WebSocket arg0, String arg1) {
		updateSize();
		writeFrame();
	}

	public synchronized void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
		activeSockets.remove(arg0);
	}

	public void onError(WebSocket arg0, Exception arg1) {
	}

}
