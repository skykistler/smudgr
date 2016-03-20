package io.smudgr.cef.controller.messages;

import java.util.HashMap;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import io.smudgr.cef.view.CefView;

public class SmudgrQueryRouter extends CefMessageRouterHandlerAdapter {
	private CefView view;
	private HashMap<String, CefMessageStrategy> messageStrategies = new HashMap<String, CefMessageStrategy>();

	public SmudgrQueryRouter(CefView v) {
		this.view = v;

		messageStrategies.put("openFileDialog", new OpenFileDialog());
		messageStrategies.put("updateRenderView", new UpdateRenderView());
		messageStrategies.put("prop", new SetProperty());
	}

	public boolean onQuery(CefBrowser browser, long query_id, String request, boolean persistent,
			CefQueryCallback callback) {

		String[] parts = request.split(":");

		String type = parts[0].toLowerCase().trim();
		String content = parts.length > 1 ? request.substring(request.indexOf(":")) : "";

		CefMessageStrategy strategy = messageStrategies.get(type);

		if (strategy == null) {
			callback.failure(0, "No message strategy found for: " + request);
			return false;
		}

		boolean result = strategy.processRequest(content, view);

		if (result)
			callback.success(strategy.onSuccess());
		else
			callback.failure(1, strategy.onFailure());

		return true;
	}

}