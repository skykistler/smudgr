package io.smudgr.extensions.automate.controls;

import io.smudgr.app.Controller;
import io.smudgr.project.smudge.param.Parameter;
import io.smudgr.project.smudge.source.Source;
import io.smudgr.project.smudge.source.SourceSet;
import io.smudgr.project.util.PropertyMap;

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
