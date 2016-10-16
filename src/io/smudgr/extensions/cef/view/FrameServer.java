package io.smudgr.extensions.cef.view;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import io.smudgr.project.util.Frame;

public class FrameServer extends WebSocketServer {

	private volatile Frame frame = null;
	private volatile boolean dimensionsChanged;
	private volatile WebSocket activeSocket;

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
		if (frame == null || f.getWidth() != frame.getWidth() || f.getHeight() != frame.getHeight())
			dimensionsChanged = true;

		if (frame != null)
			frame.dispose();

		frame = f.copy();

		bufferSize = frame.pixels.length * 4;
		if (buffer == null || buffer.capacity() != bufferSize) {
			buffer = ByteBuffer.allocate(bufferSize);
			buffer.order(ByteOrder.BIG_ENDIAN);
		}
	}

	public void writeFrame() {
		if (activeSocket.hasBufferedData() || writing || frame == null || activeSocket == null)
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

		buffer.flip();
		activeSocket.send(buffer);

		toDraw.dispose();

		writing = false;
	}

	private void updateSize() {
		activeSocket.send("w:" + frame.getWidth());
		activeSocket.send("h:" + frame.getHeight());
	}

	public void onOpen(WebSocket arg0, ClientHandshake arg1) {
		if (activeSocket != null)
			activeSocket.close(0);

		activeSocket = arg0;
		writing = false;

		updateSize();
		writeFrame();
	}

	public void onMessage(WebSocket arg0, String arg1) {
		updateSize();
		writeFrame();
	}

	public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
	}

	public void onError(WebSocket arg0, Exception arg1) {
	}

}
