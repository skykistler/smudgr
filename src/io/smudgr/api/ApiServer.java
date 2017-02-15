package io.smudgr.api;

import java.net.InetSocketAddress;

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

	private volatile WebSocket client;
	private ApiInvoker invoker;

	/**
	 * Create a new {@link ApiServer} on a random open port using the given
	 * {@link ApiInvoker}
	 *
	 * @param invoker
	 *            {@link ApiInvoker}
	 */
	public ApiServer(ApiInvoker invoker) {
		this(invoker, 0);
	}

	/**
	 * Create a new {@link ApiServer} using the given {@link ApiInvoker} on
	 * a specific port
	 *
	 * @param invoker
	 *            {@link ApiInvoker}
	 * @param port
	 *            {@code int}
	 */
	public ApiServer(ApiInvoker invoker, int port) {
		super(new InetSocketAddress(port));
		this.invoker = invoker;
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		client = conn;
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
		if (client == null)
			return;

		client.send(message.toString());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {

	}
}
