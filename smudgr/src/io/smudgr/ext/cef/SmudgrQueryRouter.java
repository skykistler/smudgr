package io.smudgr.ext.cef;

import java.util.HashMap;
import java.util.Set;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import io.smudgr.ext.cef.messages.CefMessageStrategy;
import io.smudgr.reflect.Reflect;

public class SmudgrQueryRouter extends CefMessageRouterHandlerAdapter {

	private HashMap<String, CefMessageStrategy> messageStrategies = new HashMap<String, CefMessageStrategy>();

	public SmudgrQueryRouter() {
		Reflect<CefMessageStrategy> messageReflection = new Reflect<CefMessageStrategy>("io.smudgr.ext.cef.controller.messages");

		Set<Class<?>> messages = messageReflection.get();
		for (Class<?> c : messages) {
			try {
				CefMessageStrategy strategy = (CefMessageStrategy) c.newInstance();

				messageStrategies.put(strategy.getCommand(), strategy);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean onQuery(CefBrowser browser, long query_id, String request, boolean persistent, CefQueryCallback callback) {
		String[] parts = request.split(":");

		String type = parts[0].toLowerCase().trim();
		String content = parts.length > 1 ? request.substring(request.indexOf(":")) : "";

		CefMessageStrategy strategy = messageStrategies.get(type);

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