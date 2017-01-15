package io.smudgr.api;

import io.smudgr.util.Frame;
import io.smudgr.util.output.ImageOutput;

public class ControlSaveFrame implements ApiCommand {

	public String getCommand() {
		return "control.saveFrame";
	}

	public ApiMessage execute(ApiMessage data) {

		Frame frame = getProject().getSmudge().getFrame();

		if (frame == null)
			return null;

		ImageOutput out = new ImageOutput("frame", frame.getWidth(), frame.getHeight());
		out.addFrame(frame);

		return null;
	}

}
