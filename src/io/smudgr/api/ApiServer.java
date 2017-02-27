package io.smudgr.api;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * The {@link ApiServer} uses an underlying {@link WebSocketServer} to allow
 * front-end JavaScript to execute API commands via the {@link ApiInvoker}
 */
public class ApiServer extends WebSocketServer {

	/**
	 * Default API_PORT to use when not mutexing.
	 */
	public static final int API_PORT = 45455;

	private List<WebSocket> connections = Collections.synchronizedList(new ArrayList<WebSocket>());
	private ApiInvoker invoker;

	/**
	 * Create a new {@link ApiServer} on a random open port.
	 *
	 */
	public ApiServer() {
		this(0);
	}

	/**
	 * Create a new {@link ApiServer} on a specific port
	 *
	 * @param port
	 *            {@code int}
	 */
	public ApiServer(int port) {
		super(new InetSocketAddress(port));
	}

	/**
	 * Set the {@link ApiInvoker} to use for this {@link ApiServer}
	 *
	 * @param invoker
	 *            {@link ApiInvoker}
	 */
	public void setInvoker(ApiInvoker invoker) {
		this.invoker = invoker;
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println("Connected to client: " + conn.getRemoteSocketAddress());
		connections.add(conn);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		sendMessage(invoker.invoke(message));
	}

	/**
	 * Send an {@link ApiMessage} to all connected clients.
	 *
	 * @param message
	 *            {@link ApiMessage}
	 */
	public void sendMessage(ApiMessage message) {
		synchronized (connections) {
			for (WebSocket client : connections)
				if (client.isOpen())
					client.send(message.toString());
		}
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		connections.remove(conn);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
	}
}
