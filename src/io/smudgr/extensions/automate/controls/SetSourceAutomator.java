package io.smudgr.extensions.automate.controls;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.param.Parameter;
import io.smudgr.util.source.Source;
import io.smudgr.util.source.SourceSet;

/**
 * I'm honestly not too sure about this class. Get rid of this shit lol
 */
public class SetSourceAutomator implements AutomatorControl {

	@Override
	public String getName() {
		return "Set Source";
	}

	private int sourceIndex = 0;

	/**
	 *
	 * @param i
	 *            index
	 */
	public void setSourceIndex(int i) {
		sourceIndex = i;
	}

	@Override
	public void inputValue(int value) {
	}

	@Override
	public void inputOn() {
		Source src = Controller.getInstance().getProject().getSmudge().getSource();

		if (src instanceof SourceSet)
			((SourceSet) src).setCurrentSource(sourceIndex);
	}

	@Override
	public void inputOff() {
	}

	@Override
	public void increment() {
	}

	@Override
	public void decrement() {
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
	}

	@Override
	public Parameter getParameter() {
		return null;
	}

	@Override
	public void save(PropertyMap pm) {
		pm.setAttribute("sourceIndex", sourceIndex);
	}

	@Override
	public void load(PropertyMap pm) {
		if (pm.hasAttribute("sourceIndex"))
			sourceIndex = Integer.parseInt(pm.getAttribute("sourceIndex"));
	}

}
