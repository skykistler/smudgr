package io.smudgr.extensions.cef.util;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import io.smudgr.api.ApiInvoker;
import io.smudgr.api.ApiMessage;

/**
 * The {@link CefQueryHandler} takes advantage of CEF's ability to hook a
 * cefQuery() function on the window object in the front-end.
 * <p>
 * If the front-end sends a message using window.cefQuery(string), the current
 * {@link ApiInvoker} will be used to evaluate the message.
 */
public class CefQueryHandler extends CefMessageRouterHandlerAdapter {

	private ApiInvoker invoker;

	/**
	 * Create a new query listener using the given {@link ApiInvoker}
	 *
	 * @param invoker
	 *            {@link ApiInvoker}
	 */
	public CefQueryHandler(ApiInvoker invoker) {
		this.invoker = invoker;
	}

	@Override
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
