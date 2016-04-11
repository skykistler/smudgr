package io.smudgr.extensions.cef.commands;

import io.smudgr.extensions.cef.view.RenderFrame;

public class CanvasResize implements CefCommand {

	public String getCommand() {
		return "canvas.size";
	}

	public boolean request(String content) {
		String[] parts = content.split(":");
		if (parts.length != 4)
			return false;

		RenderFrame canvas = RenderFrame.getInstance();

		canvas.setX(parts[0]);
		canvas.setY(parts[1]);
		canvas.setWidth(parts[2]);
		canvas.setHeight(parts[3]);

		canvas.updateDimensions();

		return true;
	}

	public String onSuccess() {
		return "Updated render canvas";
	}

	public String onFailure() {
		return "Malformed payload";
	}

}
