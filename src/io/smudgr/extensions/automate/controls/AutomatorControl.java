package io.smudgr.extensions.automate.controls;

import io.smudgr.app.Controllable;
import io.smudgr.project.ProjectItem;
import io.smudgr.project.smudge.param.Parameter;

public interface AutomatorControl extends Controllable, ProjectItem {
	
	public static final String PROPERTY_MAP_KEY = "automator";

	public void init();

	public void update();

	public Parameter getParameter();
}
