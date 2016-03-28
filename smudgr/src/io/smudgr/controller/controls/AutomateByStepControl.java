package io.smudgr.controller.controls;

import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.AlgorithmComponent;
import io.smudgr.source.smudge.param.NumberParameter;
import io.smudgr.source.smudge.param.Parameter;

public class AutomateByStepControl extends Controllable {

	private NumberParameter parameter;
	private boolean run = true;

	private double increment, speed;

	public AutomateByStepControl(Parameter p) {
		this(p, .25);
	}

	public AutomateByStepControl(Parameter p, double increment) {
		super(p.toString() + " Automator");
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

	public void setProperties() {
		AlgorithmComponent component = ((AlgorithmComponent) parameter.getParent());
		Algorithm algorithm = component.getAlgorithm();
		String parameterName = algorithm.getID() + ":" + component.getID() + ":" + parameter;

		getPropertyMap().setProperty("parameter", parameterName);
		getPropertyMap().setProperty("increment", increment);
		getPropertyMap().setProperty("speed", speed);
		getPropertyMap().setProperty("run", run);
	}

	public void getProperties() {
		increment = Double.parseDouble(getPropertyMap().getProperty("increment"));
		speed = Double.parseDouble(getPropertyMap().getProperty("speed"));
		run = Boolean.parseBoolean(getPropertyMap().getProperty("run"));

		String[] parameter_id = getPropertyMap().getProperty("parameter").split(":");
		int alg_id = Integer.parseInt(parameter_id[0]);
		int component_id = Integer.parseInt(parameter_id[1]);
		String parameterName = parameter_id[2];

		parameter = (NumberParameter) getController().getSmudge().getAlgorithm(alg_id).getComponent(component_id)
				.getParameter(parameterName);

		setName(parameter + " Animator");
	}

}
