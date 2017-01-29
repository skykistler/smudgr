package io.smudgr.extensions.cef;

import java.io.IOException;

import io.smudgr.api.ApiInvoker;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.extensions.ControllerExtension;
import io.smudgr.extensions.cef.util.CommandServer;

/**
 * The {@link CefExtension} provides integration with the Chromium Extendable
 * Framework.
 * <p>
 * This allows smudgr to communicate with a front-end built on web
 * frameworks, via an internal stand-alone stripped down build of Chromium.
 */
public class CefExtension implements ControllerExtension {

	@Override
	public String getName() {
		return "CEF";
	}

	private CommandServer server;

	/**
	 * Initializes the command API.
	 * <p>
	 * TODO: Move the CommandServer to some vanilla package
	 */
	public CefExtension() {
		server = new CommandServer(getInvoker(), 45455);
		server.start();
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
	}

	@Override
	public void stop() {
		try {
			server.stop();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessage(ApiMessage message) {
		getServer().sendMessage(message);
	}

	/**
	 * Gets the {@link ApiInvoker} for the current application instance.
	 *
	 * @return {@link ApiInvoker}
	 * @see Controller#getApiInvoker()
	 */
	public ApiInvoker getInvoker() {
		return Controller.getInstance().getApiInvoker();
	}

	/**
	 * Gets the WebSocket server being used to communicate with the front-end.
	 *
	 * @return {@link CommandServer}
	 */
	public CommandServer getServer() {
		return server;
	}

	@Override
	public void save(PropertyMap pm) {

	}

	@Override
	public void load(PropertyMap pm) {

	}

}
