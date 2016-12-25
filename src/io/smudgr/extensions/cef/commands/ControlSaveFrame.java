package io.smudgr.extensions.cef.commands;

import io.smudgr.app.output.ImageOutput;
import io.smudgr.extensions.cef.util.CefMessage;
import io.smudgr.project.util.Frame;

public class ControlSaveFrame implements CefCommand {

	public String getCommand() {
		return "control.saveFrame";
	}

	public CefMessage execute(CefMessage data) {

		Frame frame = getProject().getSmudge().getFrame();

		if (frame == null)
			return null;

		ImageOutput out = new ImageOutput("frame", frame.getWidth(), frame.getHeight());
		out.addFrame(frame);

		return null;
	}

}
