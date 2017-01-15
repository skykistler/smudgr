package io.smudgr.extensions.cef;

import java.io.IOException;
import java.net.UnknownHostException;

import io.smudgr.api.ApiInvoker;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.Controller;
import io.smudgr.extensions.ControllerExtension;
import io.smudgr.extensions.cef.util.CommandServer;
import io.smudgr.project.util.PropertyMap;

public class CefExtension implements ControllerExtension {

	public String getName() {
		return "CEF";
	}

	private CommandServer server;

	public CefExtension() {
		try {
			server = new CommandServer(getInvoker(), 45455);
			server.start();
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}
	}

	public void init() {
	}

	public void update() {
	}

	public void stop() {
		try {
			server.stop();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(ApiMessage message) {
		getServer().sendMessage(message);
	}

	public ApiInvoker getInvoker() {
		return Controller.getInstance().getApiInvoker();
	}

	public CommandServer getServer() {
		return server;
	}

	public void save(PropertyMap pm) {

	}

	public void load(PropertyMap pm) {

	}

}
