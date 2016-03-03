package io.smudgr.view.cef;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import io.smudgr.view.View;

public class SmudgrQueryRouter extends CefMessageRouterHandlerAdapter {
	private View view;

	public SmudgrQueryRouter(View v) {
		this.view = v;
	}

	public boolean onQuery(CefBrowser browser, long query_id, String request, boolean persistent, CefQueryCallback callback) {

		if (request.startsWith("smudgr://") && request.endsWith("html")) {
			JarFile resource = new JarFile(request.substring(9));
			byte[] data = resource.getData();
			String file = new String(data);

			callback.success(file);
		}

		return true;
	}
}