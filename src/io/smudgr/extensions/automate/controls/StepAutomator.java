package io.smudgr.extensions.automate.controls;

import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.engine.param.Parameter;

/**
 * The {@link StepAutomator} simply changes a {@link NumberParameter} by it's
 * {@link NumberParameter#getStep()} every tick.
 * <p>
 * The {@link NumberParameter#getStep()} is multiplied by a controllable speed
 * property, which is adjusted with {@link StepAutomator#increment()} and
 * {@link StepAutomator#decrement()}
 */
public class StepAutomator implements AutomatorControl {

	@Override
	public String getElementName() {
		return "Animate";
	}

	@Override
	public String getElementIdentifier() {
		return "step";
	}

	private NumberParameter parameter;
	private boolean run = true;

	private double increment = .05, speed = increment;

	@Override
	public void init() {

	}

	@Override
	public void update() {
		if (!run)
			return;

		double val = parameter.getValue();
		double step = parameter.getStep();
		val = val + step * speed;

		parameter.setValue(val);
	}

	@Override
	public void inputValue(int value) {

	}

	@Override
	public void inputOn() {
		run = true;
	}

	@Override
	public void inputOff() {
		run = false;
	}

	@Override
	public void increment() {
		run = true;

		speed += increment;
	}

	@Override
	public void decrement() {
		speed -= increment;
		if (speed < 0)
			speed = 0;
	}

	@Override
	public Parameter getParameter() {
		return parameter;
	}

	@Override
	public void save(PropertyMap pm) {
		int parameterId = getProject().getId(parameter);

		pm.setAttribute("parameter", parameterId);
		pm.setAttribute("increment", increment);
		pm.setAttribute("speed", speed);
		pm.setAttribute("run", run);
	}

	@Override
	public void load(PropertyMap pm) {
		if (pm.hasAttribute("increment"))
			increment = Double.parseDouble(pm.getAttribute("increment"));

		if (pm.hasAttribute("speed"))
			speed = Double.parseDouble(pm.getAttribute("speed"));

		if (pm.hasAttribute("run"))
			run = Boolean.parseBoolean(pm.getAttribute("run"));

		if (pm.hasAttribute("parameter")) {
			int parameterId = Integer.parseInt(pm.getAttribute("parameter"));

			parameter = (NumberParameter) getProject().getItem(parameterId);
		}
	}

}
