package io.smudgr.controller.controls;

import io.smudgr.smudge.param.NumberParameter;
import io.smudgr.smudge.param.Parameter;

public class AutomateByStepControl extends Controllable {

	public String getName() {
		String name = "";
		if (parameter != null)
			name += parameter.getParent() + " - " + parameter + " ";

		name += "Automator";

		return name;
	}

	private NumberParameter parameter;
	private boolean run = true;

	private double increment, speed;

	public AutomateByStepControl(Parameter p) {
		this(p, .25);
	}

	public AutomateByStepControl(Parameter p, double increment) {
		parameter = (NumberParameter) p;

		this.increment = increment;
		this.speed = 1;

		requestBind();
	}

	public void update() {
		if (!run)
			return;

		double val = parameter.getValue();
		double step = parameter.getStep();
		val = val + step * speed;

		parameter.setValue(val);
	}

	public void inputValue(int value) {

	}

	public void inputOn(int value) {
		run = true;
	}

	public void inputOff(int value) {
		run = false;
	}

	public void increment() {
		run = true;

		speed += increment;
	}

	public void decrement() {
		speed -= increment;
		if (speed < 0)
			speed = 0;
	}

	public void savePropertyMap() {
		int parameterId = getIdManager().getId(parameter);

		getPropertyMap().setProperty("parameter", parameterId);
		getPropertyMap().setProperty("increment", increment);
		getPropertyMap().setProperty("speed", speed);
		getPropertyMap().setProperty("run", run);
	}

	public void loadPropertyMap() {
		increment = Double.parseDouble(getPropertyMap().getProperty("increment"));
		speed = Double.parseDouble(getPropertyMap().getProperty("speed"));
		run = Boolean.parseBoolean(getPropertyMap().getProperty("run"));

		int parameterId = Integer.parseInt(getPropertyMap().getProperty("parameter"));

		parameter = (NumberParameter) getIdManager().getComponent(parameterId);
	}

}
