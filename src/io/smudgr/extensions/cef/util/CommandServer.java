package io.smudgr.extensions.cef.util;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import io.smudgr.api.ApiInvoker;
import io.smudgr.api.ApiMessage;

public class CommandServer extends WebSocketServer {

	private volatile WebSocket client;
	private ApiInvoker invoker;

	public CommandServer(ApiInvoker invoker) throws UnknownHostException {
		this(invoker, 0);
	}

	public CommandServer(ApiInvoker invoker, int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
		this.invoker = invoker;
	}

	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		client = conn;
	}

	public void onMessage(WebSocket conn, String message) {
		sendMessage(invoker.invoke(message));
	}

	public void sendMessage(ApiMessage message) {
		if (client == null)
			return;

		client.send(message.toString());
	}

	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
	}

	public void onError(WebSocket conn, Exception ex) {

	}
}
