package io.smudgr.extensions.automate.controls;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.smudge.param.Parameter;
import io.smudgr.util.source.Source;
import io.smudgr.util.source.SourceSet;

public class SetSourceAutomator implements AutomatorControl {

	public String getName() {
		return "Set Source";
	}

	private int sourceIndex = 0;

	public void setSourceIndex(int i) {
		sourceIndex = i;
	}

	public void inputValue(int value) {
	}

	public void inputOn() {
		Source src = Controller.getInstance().getProject().getSmudge().getSource();

		if (src instanceof SourceSet)
			((SourceSet) src).setCurrentSource(sourceIndex);
	}

	public void inputOff() {
	}

	public void increment() {
	}

	public void decrement() {
	}

	public void init() {
	}

	public void update() {
	}

	public Parameter getParameter() {
		return null;
	}

	public void save(PropertyMap pm) {
		pm.setAttribute("sourceIndex", sourceIndex);
	}

	public void load(PropertyMap pm) {
		if (pm.hasAttribute("sourceIndex"))
			sourceIndex = Integer.parseInt(pm.getAttribute("sourceIndex"));
	}

}
