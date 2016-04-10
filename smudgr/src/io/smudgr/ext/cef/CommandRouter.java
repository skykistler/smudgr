package io.smudgr.ext.cef;

import java.util.HashMap;
import java.util.Set;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import io.smudgr.ext.cef.messages.CefCommand;
import io.smudgr.reflect.Reflect;

public class CommandRouter extends CefMessageRouterHandlerAdapter {

	private HashMap<String, CefCommand> commands = new HashMap<String, CefCommand>();

	public CommandRouter() {
		Reflect messageReflection = new Reflect("io.smudgr.ext.cef.messages", CefCommand.class);

		Set<Class<?>> messages = messageReflection.get();
		for (Class<?> c : messages) {
			try {
				CefCommand command = (CefCommand) c.newInstance();
				commands.put(command.getCommand(), command);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean onQuery(CefBrowser browser, long query_id, String request, boolean persistent, CefQueryCallback callback) {
		String[] parts = request.split(":");

		String type = parts[0].toLowerCase().trim();
		String content = parts.length > 1 ? request.substring(request.indexOf(":") + 1) : "";

		CefCommand strategy = commands.get(type);

		if (strategy == null) {
			callback.failure(0, "No message strategy found for: " + request);
			return false;
		}

		boolean result = strategy.request(content);

		if (result)
			callback.success(strategy.onSuccess());
		else
			callback.failure(1, strategy.onFailure());

		return true;
	}

}