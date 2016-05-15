package io.smudgr.extensions.automate.controls;

import io.smudgr.project.PropertyMap;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.project.smudge.param.Parameter;
import io.smudgr.project.smudge.param.UnivariateParameter;

public class EasingAutomator implements AutomatorControl {

	public String getName() {
		return "Ease";
	}

	private NumberParameter parameter;
	private UnivariateParameter easingFunction;

	private double increment = .05, speed = increment;

	public void init() {

	}

	public void update() {
		double val = parameter.getValue();
		double step = parameter.getStep();
		val = val + step * speed;

		parameter.setValue(val);
	}

	public void inputValue(int value) {

	}

	public void inputOn() {
	}

	public void inputOff() {
	}

	public void increment() {
		speed += increment;
	}

	public void decrement() {
		speed -= increment;
		if (speed < 0)
			speed = 0;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void save(PropertyMap pm) {
		int parameterId = getProject().getId(parameter);

		pm.setAttribute("parameter", parameterId);
		pm.setAttribute("increment", increment);
		pm.setAttribute("speed", speed);
	}

	public void load(PropertyMap pm) {
		if (pm.hasAttribute("increment"))
			increment = Double.parseDouble(pm.getAttribute("increment"));

		if (pm.hasAttribute("speed"))
			speed = Double.parseDouble(pm.getAttribute("speed"));

		if (pm.hasAttribute("parameter")) {
			int parameterId = Integer.parseInt(pm.getAttribute("parameter"));

			parameter = (NumberParameter) getProject().getElement(parameterId);
		}
	}

}
