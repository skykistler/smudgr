package io.smudgr.extensions.cef.util;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import io.smudgr.api.ApiInvoker;
import io.smudgr.api.ApiMessage;

public class CefQueryHandler extends CefMessageRouterHandlerAdapter {

	private ApiInvoker invoker;

	public CefQueryHandler(ApiInvoker invoker) {
		this.invoker = invoker;
	}

	public boolean onQuery(CefBrowser browser, long query_id, String request, boolean persistent, CefQueryCallback callback) {
		ApiMessage response = invoker.invoke(request);

		System.out.println(response);

		if (response.get("status").equals("failure"))
			callback.failure(0, response.toString());

		if (response.get("status").equals("success"))
			callback.success(response.toString());

		return true;
	}

}
