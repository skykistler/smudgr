package io.smudgr.extensions.cef.commands;

import io.smudgr.extensions.cef.util.CefMessage;
import io.smudgr.extensions.cef.view.RenderFrame;

public class CanvasResize implements CefCommand {

	public String getCommand() {
		return "canvas.size";
	}

	public CefMessage execute(CefMessage data) {
		RenderFrame canvas = RenderFrame.getInstance();

		if (canvas == null)
			return null;

		canvas.setX((int) data.getNumber("x"));
		canvas.setY((int) data.getNumber("y"));
		canvas.setWidth((int) data.getNumber("w"));
		canvas.setHeight((int) data.getNumber("h"));

		canvas.updateDimensions();

		return null;
	}

	public String onSuccess() {
		return "Updated render canvas";
	}

	public String onFailure() {
		return "Malformed payload";
	}

}
