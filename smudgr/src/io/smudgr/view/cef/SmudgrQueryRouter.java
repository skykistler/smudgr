package io.smudgr.view.cef;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import io.smudgr.source.smudge.Smudge;
import io.smudgr.view.View;

public class SmudgrQueryRouter extends CefMessageRouterHandlerAdapter {
	private View view;

	public SmudgrQueryRouter(View v) {
		this.view = v;
	}

	public boolean onQuery(CefBrowser browser, long query_id, String request, boolean persistent, CefQueryCallback callback) {

		Smudge s = view.getController().getSmudge();
		s.setEnabled(!s.isEnabled());
		callback.success("Smudged is " + (s.isEnabled() ? "enabled" : "disabled"));

		return true;
	}
}