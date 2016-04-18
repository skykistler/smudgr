package io.smudgr.extensions.automate.controls;

import io.smudgr.app.Controller;
import io.smudgr.project.PropertyMap;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.project.smudge.param.Parameter;

public class AutomateByBeatControl implements AutomatorControl {

	private static final double[] SPEEDS = { 1 / 32.0, 1 / 16.0, 1 / 8.0, 1 / 4.0, 1 / 2.0, 1.0, 2.0, 4.0, 8.0 };

	public String getName() {
		return "Beat Sync";
	}

	private NumberParameter parameter;
	private boolean run = true;

	private int speed = 5;

	public void init() {

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

	public void inputOn() {
		run = true;
	}

	public void inputOff() {
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

	public Parameter getParameter() {
		return parameter;
	}

	public void save(PropertyMap pm) {
		int parameterId = getProject().getId(parameter);

		pm.setAttribute("parameter", parameterId);
		pm.setAttribute("speed", speed);
		pm.setAttribute("run", run);
	}

	public void load(PropertyMap pm) {
		if (pm.hasAttribute("speed"))
			speed = Integer.parseInt(pm.getAttribute("speed"));

		if (pm.hasAttribute("run"))
			run = Boolean.parseBoolean(pm.getAttribute("run"));

		if (pm.hasAttribute("parameter")) {
			int parameterId = Integer.parseInt(pm.getAttribute("parameter"));

			parameter = (NumberParameter) getProject().getElement(parameterId);
		}
	}

}
