package io.smudgr.project.smudge.source;

import io.smudgr.app.Controller;
import io.smudgr.project.SourceLibrary;
import io.smudgr.util.Frame;

public interface Source {
	public void init();

	public void update();

	public Frame getFrame();

	public void dispose();

	public default SourceLibrary getSourceLibrary() {
		return Controller.getInstance().getProject().getSourceLibrary();
	}

}
