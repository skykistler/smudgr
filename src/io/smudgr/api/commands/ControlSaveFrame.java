package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.util.PixelFrame;
import io.smudgr.util.output.ImageOutput;

/**
 * Capture and save the most recent {@link PixelFrame}.
 *
 * @see ControlRecord
 */
public class ControlSaveFrame implements ApiCommand {

	@Override
	public String getCommand() {
		return "control.saveFrame";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		PixelFrame frame = (PixelFrame) getProject().getRack().getLastFrame();

		if (frame == null)
			return null;

		ImageOutput out = new ImageOutput("frame", frame.getWidth(), frame.getHeight());
		out.addFrame(frame);

		return null;
	}

}
