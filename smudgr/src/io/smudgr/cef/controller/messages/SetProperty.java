package io.smudgr.cef.controller.messages;

import io.smudgr.cef.view.CefView;

public class SetProperty implements CefMessageStrategy {

	private String property;
	private String value;

	@Override
	public boolean processRequest(String content, CefView view) {
		String[] parts = content.split(":");

		if (parts.length != 3)
			return false;

		property = parts[1];
		value = parts[2];

		switch (property) {
		case "renderOffsetX":
			view.getWindow().getRenderFrame().setX(value);
			break;
		case "renderOffsetY":
			view.getWindow().getRenderFrame().setY(value);
			break;
		case "renderViewWidth":
			view.getWindow().getRenderFrame().setWidth(value);
			break;
		case "renderViewHeight":
			view.getWindow().getRenderFrame().setHeight(value);
			break;
		default:
			return false;
		}

		return true;
	}

	@Override
	public String onSuccess() {
		return "Set property " + property + " to " + value;
	}

	@Override
	public String onFailure() {
		return "Invalid property value";
	}

}
