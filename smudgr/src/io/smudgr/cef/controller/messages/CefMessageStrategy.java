package io.smudgr.cef.controller.messages;

import io.smudgr.cef.view.CefView;

public interface CefMessageStrategy {
	public boolean processRequest(String content, CefView view);

	public String onSuccess();

	public String onFailure();
}
