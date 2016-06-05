package io.smudgr.extensions.cef.util;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import io.smudgr.extensions.cef.CommandInvoker;

public class CefQueryHandler extends CefMessageRouterHandlerAdapter {

	private CommandInvoker invoker;

	public CefQueryHandler(CommandInvoker invoker) {
		this.invoker = invoker;
	}

	public boolean onQuery(CefBrowser browser, long query_id, String request, boolean persistent, CefQueryCallback callback) {
		CefMessage response = invoker.invoke(request);

		if (response.get("status").equals("failure"))
			callback.failure(0, response.toString());

		if (response.get("status").equals("success"))
			callback.success(response.toString());

		return true;
	}

}
