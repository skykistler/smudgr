package io.smudgr.cef.controller.messages;

import io.smudgr.cef.view.CefView;

public class UpdateRenderView implements CefMessageStrategy {

	@Override
	public boolean processRequest(String content, CefView view) {
		view.getWindow().getRenderFrame().updateDimensions();
		return true;
	}

	@Override
	public String onSuccess() {
		return "Updated render view";
	}

	@Override
	public String onFailure() {
		return "Failed to update render view";
	}

}
