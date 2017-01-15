package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.extensions.cef.view.RenderFrame;

public class CanvasResize implements ApiCommand {

	public String getCommand() {
		return "canvas.size";
	}

	public ApiMessage execute(ApiMessage data) {
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

}
