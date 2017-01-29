package io.smudgr.extensions.automate.controls;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.Project;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.engine.param.Parameter;

/**
 * The {@link BeatAutomator} synchronizes with the current
 * {@link Project#getBPM()} to adjust a {@link NumberParameter} value in sync
 * with the beat.
 */
public class BeatAutomator implements AutomatorControl {

	private static final double[] SPEEDS = { 1 / 32.0, 1 / 16.0, 1 / 8.0, 1 / 4.0, 1 / 2.0, 1.0, 2.0, 4.0, 8.0 };

	@Override
	public String getName() {
		return "Beat Sync";
	}

	private NumberParameter parameter;
	private boolean run = true;

	private int speed = 5;

	@Override
	public void init() {

	}

	@Override
	public void update() {
		if (!run)
			return;

		double val = parameter.getValue();
		double step = parameter.getStep();
		val = val + step / (Controller.TICKS_PER_BEAT * SPEEDS[speed]);

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

		speed--;
		if (speed < 0)
			speed = 0;
	}

	@Override
	public void decrement() {
		speed++;

		if (speed >= SPEEDS.length) {
			run = false;
			speed = SPEEDS.length - 1;
		}
	}

	@Override
	public Parameter getParameter() {
		return parameter;
	}

	@Override
	public void save(PropertyMap pm) {
		int parameterId = getProject().getId(parameter);

		pm.setAttribute("parameter", parameterId);
		pm.setAttribute("speed", speed);
		pm.setAttribute("run", run);
	}

	@Override
	public void load(PropertyMap pm) {
		if (pm.hasAttribute("speed"))
			speed = Integer.parseInt(pm.getAttribute("speed"));

		if (pm.hasAttribute("run"))
			run = Boolean.parseBoolean(pm.getAttribute("run"));

		if (pm.hasAttribute("parameter")) {
			int parameterId = Integer.parseInt(pm.getAttribute("parameter"));

			parameter = (NumberParameter) getProject().getItem(parameterId);
		}
	}

}
