package io.smudgr.extensions.cef.view;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.util.DisposedBytesProvider;

import io.smudgr.util.Frame;

/**
 * The {@link FrameServer} provides a {@link WebSocketServer} for streaming
 * video data out of smudgr.
 */
public class FrameServer extends WebSocketServer {

	private List<WebSocket> connections = Collections.synchronizedList(new ArrayList<WebSocket>());

	private volatile Frame frame = null;
	private volatile boolean dimensionsChanged;

	private ByteBuffer buffer = null;
	private int bufferSize, i, color, r, g;

	/**
	 * Create a new {@link FrameServer} on any open port
	 */
	public FrameServer() {
		this(0);
	}

	/**
	 * Create a new {@link FrameServer} on the given port
	 *
	 * @param port
	 *            Desired port to start the server on
	 */
	public FrameServer(int port) {
		super(new InetSocketAddress(port));
	}

	/**
	 * Sets the current {@link Frame} to stream
	 *
	 * @param f
	 *            current {@link Frame}
	 */
	public synchronized void setFrame(Frame f) {
		Frame resized = f.fitToSize(600, 600, false);

		if (frame == null || resized.getWidth() != frame.getWidth() || resized.getHeight() != frame.getHeight())
			dimensionsChanged = true;

		if (frame != null)
			frame.dispose();

		frame = resized;

		bufferSize = 8 + frame.pixels.length * 4;
		if (buffer == null || buffer.capacity() != bufferSize) {

			// It ain't easy.. bein cheesy
			if (buffer != null)
				DisposedBytesProvider.getInstance().disposeBytes(buffer);

			buffer = DisposedBytesProvider.getInstance().getDisposedBytes(bufferSize, true);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
		}
	}

	/**
	 * Write out the current {@link Frame} to all clients
	 */
	public synchronized void writeFrame() {
		if (frame == null || connections.size() == 0)
			return;

		if (dimensionsChanged) {
			updateSize();
			dimensionsChanged = false;
		}

		buffer.position(0);

		buffer.putInt(frame.getWidth());
		buffer.putInt(frame.getHeight());

		for (i = 0; i < frame.pixels.length; i++) {
			color = frame.pixels[i];

			color = ((color & 0xFF00FF00)) |
					((color & 0x00FF0000) >> 16) |
					((color & 0x000000FF) << 16);

			buffer.putInt(color);
		}

		synchronized (connections) {
			for (WebSocket client : connections) {
				buffer.rewind();
				sendIfReady(client, buffer);
			}
		}
	}

	private synchronized void updateSize() {
		// synchronized (connections) {
		// for (WebSocket client : connections) {
		// sendIfOpen(client, "{\"w\":" + frame.getWidth() + ",\"h\":" +
		// frame.getHeight() + "}");
		// }
		// }
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		connections.add(conn);

		updateSize();
		writeFrame();
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		// Don't care about the message, just respond with frame data
		updateSize();
		writeFrame();
	}

	@Override
	public void onClose(WebSocket conn, int arg1, String arg2, boolean arg3) {
		connections.remove(conn);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
	}

	// private void sendIfOpen(WebSocket client, String message) {
	// if (client.isOpen())
	// client.send(message);
	// }

	private void sendIfReady(WebSocket client, ByteBuffer buffer) {
		if (client.isOpen() && !client.hasBufferedData())
			client.send(buffer);
	}

}
