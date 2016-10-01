package io.smudgr.extensions.cef.util;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import io.smudgr.extensions.cef.CommandInvoker;

public class CommandServer extends WebSocketServer {

	private volatile WebSocket client;
	private CommandInvoker invoker;

	public CommandServer(CommandInvoker invoker) throws UnknownHostException {
		this(invoker, 0);
	}

	public CommandServer(CommandInvoker invoker, int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
		this.invoker = invoker;
	}

	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		client = conn;
	}

	public void onMessage(WebSocket conn, String message) {
		sendMessage(invoker.invoke(message));
	}

	public void sendMessage(CefMessage message) {
		if (client == null)
			return;

		client.send(message.toString());
	}

	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
	}

	public void onError(WebSocket conn, Exception ex) {

	}
}
