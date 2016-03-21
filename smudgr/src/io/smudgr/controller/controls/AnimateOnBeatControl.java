package io.smudgr.controller.controls;

import io.smudgr.controller.BaseController;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.AlgorithmComponent;
import io.smudgr.source.smudge.param.NumberParameter;
import io.smudgr.source.smudge.param.Parameter;

public class AnimateOnBeatControl extends Controllable {

	private static final double[] SPEEDS = { 1 / 32.0, 1 / 16.0, 1 / 8.0, 1 / 4.0, 1 / 2.0, 1.0, 2.0, 4.0, 8.0 };

	private NumberParameter parameter;
	private boolean run = true;

	private int speed = 5;

	public AnimateOnBeatControl() {
		super("Animator");
	}

	public AnimateOnBeatControl(Parameter p) {
		super(p.getParent() + " - " + p.toString() + " Animator");
		parameter = (NumberParameter) p;

		requestBind();
	}

	public void update() {
		if (!run)
			return;

		double val = parameter.getValue();
		double step = parameter.getStep();
		val = val + step / (BaseController.TICKS_PER_BEAT * SPEEDS[speed]);

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

		speed--;
		if (speed < 0)
			speed = 0;
	}

	public void decrement() {
		speed++;

		if (speed >= SPEEDS.length) {
			run = false;
			speed = SPEEDS.length - 1;
		}
	}

	public void setProperties() {
		AlgorithmComponent component = ((AlgorithmComponent) parameter.getParent());
		Algorithm algorithm = component.getAlgorithm();
		String parameterName = algorithm.getID() + ":" + component.getID() + ":" + parameter;

		getPropertyMap().setProperty("parameter", parameterName);
		getPropertyMap().setProperty("speed", speed);
		getPropertyMap().setProperty("run", run);
	}

	public void getProperties() {
		speed = Integer.parseInt(getPropertyMap().getProperty("speed"));
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
