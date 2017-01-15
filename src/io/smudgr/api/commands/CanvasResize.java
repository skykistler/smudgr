package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.extensions.cef.view.RenderFrame;

/**
 * Inform the view of the current size of the canvas DOM container.
 * Takes x, y, w, h as arguments.
 * <p>
 * This is used for hardware accelerated border-less window rendering and will
 * probably be removed soon.
 */
public class CanvasResize implements ApiCommand {

	@Override
	public String getCommand() {
		return "canvas.size";
	}

	@Override
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
