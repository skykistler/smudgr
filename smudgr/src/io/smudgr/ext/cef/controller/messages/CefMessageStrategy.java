package io.smudgr.ext.cef.controller.messages;

import io.smudgr.ext.cef.view.CefView;

public interface CefMessageStrategy {
	public boolean processRequest(String content, CefView view);

	public String onSuccess();

	public String onFailure();
}
