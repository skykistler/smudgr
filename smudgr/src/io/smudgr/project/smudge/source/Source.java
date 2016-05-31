package io.smudgr.project.smudge.source;

import io.smudgr.app.Controller;
import io.smudgr.project.smudge.util.Frame;
import io.smudgr.project.smudge.util.SourceLibrary;

public interface Source {
	public void init();

	public void update();

	public default Frame getFrame() {
		return getFrame(1);
	}

	public Frame getFrame(double resizeFactor);

	public void dispose();

	public default SourceLibrary getSourceLibrary() {
		return Controller.getInstance().getProject().getSourceLibrary();
	}

}
