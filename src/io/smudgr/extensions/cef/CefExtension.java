package io.smudgr.extensions.cef;

import java.io.IOException;
import java.net.UnknownHostException;

import io.smudgr.extensions.ControllerExtension;
import io.smudgr.extensions.cef.util.CommandServer;
import io.smudgr.project.PropertyMap;

public class CefExtension implements ControllerExtension {

	public String getName() {
		return "CEF";
	}

	private CommandInvoker invoker;
	private CommandServer server;

	public CefExtension() {
		invoker = new CommandInvoker();

		try {
			server = new CommandServer(invoker, 45455);
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

	public CommandInvoker getInvoker() {
		return invoker;
	}

	public CommandServer getServer() {
		return server;
	}

	public void save(PropertyMap pm) {

	}

	public void load(PropertyMap pm) {

	}

}
