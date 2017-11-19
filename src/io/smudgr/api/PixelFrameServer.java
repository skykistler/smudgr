package io.smudgr.api;

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

import io.smudgr.app.controller.Controller;
import io.smudgr.util.PixelFrame;

/**
 * The {@link PixelFrameServer} provides a {@link WebSocketServer} for streaming
 * video data out of smudgr.
 */
public class PixelFrameServer extends WebSocketServer {

	private List<WebSocket> connections = Collections.synchronizedList(new ArrayList<WebSocket>());

	private volatile PixelFrame frame = null;

	private ByteBuffer buffer = null;
	private int bufferSize, i, color;

	/**
	 * Create a new {@link PixelFrameServer} on any open port
	 */
	public PixelFrameServer() {
		this(0);
	}

	/**
	 * Create a new {@link PixelFrameServer} on the given port
	 *
	 * @param port
	 *            Desired port to start the server on
	 */
	public PixelFrameServer(int port) {
		super(new InetSocketAddress(port));
	}

	/**
	 * Sets the current {@link PixelFrame} to stream
	 *
	 * @param f
	 *            current {@link PixelFrame}
	 */
	public synchronized void setFrame(PixelFrame f) {
		if (frame != null)
			frame.dispose();

		frame = f.fitToSize(600, 600, false);

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
	 * Write out the current {@link PixelFrame} to all clients
	 */
	public synchronized void writeFrame() {
		if (frame == null || connections.size() == 0)
			return;

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

				if (client.isOpen() && !client.hasBufferedData())
					client.send(buffer);
			}
		}
	}

	@Override
	public synchronized void onOpen(WebSocket conn, ClientHandshake handshake) {
		connections.add(conn);
		writeFrame();
	}

	@Override
	public synchronized void onMessage(WebSocket conn, String message) {
		writeFrame();
	}

	@Override
	public synchronized void onClose(WebSocket conn, int arg1, String arg2, boolean arg3) {
		connections.remove(conn);

		if (connections.size() == 0)
			Controller.getInstance().pause();
	}

	@Override
	public synchronized void onError(WebSocket conn, Exception ex) {
		connections.remove(conn);
		if (connections.size() == 0)
			Controller.getInstance().pause();
	}

}
