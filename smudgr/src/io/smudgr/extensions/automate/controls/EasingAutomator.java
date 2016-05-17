package io.smudgr.extensions.automate.controls;

import io.smudgr.project.PropertyMap;
import io.smudgr.project.smudge.alg.math.LinearFunction;
import io.smudgr.project.smudge.alg.math.UnivariateFunction;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.project.smudge.param.Parameter;

public class EasingAutomator implements AutomatorControl {

	public String getName() {
		return "Ease";
	}

	private NumberParameter parameter;

	private UnivariateFunction easingFunction = new LinearFunction();

	private double increment = .05, speed = increment;
	private double step, lastVal;

	public void init() {
		lastVal = parameter.getValue();
	}

	public void update() {
		step += speed;

		double val = parameter.getValue();

		// If something else changed the value, start easing again
		if (lastVal != val)
			step = 0;

		// If the value hasn't changed and our step is maxed out, return
		else if (step >= 1) {
			step = 1;
			return;
		}

		val = parameter.getMax() * easingFunction.calculate(step);
		parameter.setValue(val);
		lastVal = val;
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
