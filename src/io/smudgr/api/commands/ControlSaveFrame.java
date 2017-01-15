package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.util.Frame;
import io.smudgr.util.output.ImageOutput;

/**
 * Capture and save the most recent frame.
 */
public class ControlSaveFrame implements ApiCommand {

	@Override
	public String getCommand() {
		return "control.saveFrame";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {

		Frame frame = getProject().getSmudge().getFrame();

		if (frame == null)
			return null;

		ImageOutput out = new ImageOutput("frame", frame.getWidth(), frame.getHeight());
		out.addFrame(frame);

		return null;
	}

}
