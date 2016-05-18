package io.smudgr.extensions.automate.controls;

import io.smudgr.project.PropertyMap;
import io.smudgr.project.smudge.alg.math.BezierFunction;
import io.smudgr.project.smudge.alg.math.UnivariateFunction;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.project.smudge.param.Parameter;

public class EasingAutomator implements AutomatorControl {

	public String getName() {
		return "Ease";
	}

	private NumberParameter parameter;

	private UnivariateFunction easingFunction = new BezierFunction();

	private double increment = .05, speed = increment;
	private double step, initialVal, lastVal;

	public void init() {
		lastVal = parameter.getValue() - parameter.getMin();
	}

	public void update() {
		step += speed;

		double currentVal = parameter.getValue() - parameter.getMin();

		// If something else changed the value, start easing again
		if (lastVal != currentVal) {
			step = 0;
			initialVal = currentVal;
			// If the value hasn't changed and our step is maxed out, return
		} else if (step >= 1)
			step = 1;

		lastVal = initialVal * easingFunction.calculate(step) + parameter.getMin();
		parameter.setValue(lastVal);
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
