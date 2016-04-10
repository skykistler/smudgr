package io.smudgr.controller.controls;

import io.smudgr.controller.Controller;
import io.smudgr.smudge.param.NumberParameter;
import io.smudgr.smudge.param.Parameter;

public class AutomateByBeatControl extends Controllable {

	private static final double[] SPEEDS = { 1 / 32.0, 1 / 16.0, 1 / 8.0, 1 / 4.0, 1 / 2.0, 1.0, 2.0, 4.0, 8.0 };

	public String getName() {
		String name = "";
		if (parameter != null)
			name += parameter.getParent() + " - " + parameter + " ";

		name += "Beat Sync";

		return name;
	}

	private NumberParameter parameter;
	private boolean run = true;

	private int speed = 5;

	public AutomateByBeatControl() {
		requestBind();
	}

	public AutomateByBeatControl(Parameter p) {
		this();
		parameter = (NumberParameter) p;
	}

	public void update() {
		if (!run)
			return;

		double val = parameter.getValue();
		double step = parameter.getStep();
		val = val + step / (Controller.TICKS_PER_BEAT * SPEEDS[speed]);

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

	public void savePropertyMap() {
		int parameterId = getIdManager().getId(parameter);

		getPropertyMap().setProperty("parameter", parameterId);
		getPropertyMap().setProperty("speed", speed);
		getPropertyMap().setProperty("run", run);
	}

	public void loadPropertyMap() {
		speed = Integer.parseInt(getPropertyMap().getProperty("speed"));
		run = Boolean.parseBoolean(getPropertyMap().getProperty("run"));

		int parameterId = Integer.parseInt(getPropertyMap().getProperty("parameter"));

		parameter = (NumberParameter) getIdManager().getComponent(parameterId);
	}

}
