package io.smudgr.extensions.automate.controls;

import io.smudgr.app.Controllable;
import io.smudgr.project.ProjectElement;
import io.smudgr.project.smudge.param.Parameter;

public interface AutomatorControl extends Controllable, ProjectElement {

	public void init();

	public void update();

	public Parameter getParameter();
}
