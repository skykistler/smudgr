package io.smudgr.controller.controls;

import io.smudgr.alg.param.NumberParameter;
import io.smudgr.alg.param.Parameter;
import io.smudgr.controller.Controller;

public class AnimateOnBeatControl extends Controllable {

	private static final double[] SPEEDS = { 1 / 32.0, 1 / 16.0, 1 / 8.0, 1 / 4.0, 1 / 2.0, 1.0, 2.0, 4.0, 8.0 };

	private NumberParameter parameter;
	private boolean run = true;

	private int speed = 5;

	public AnimateOnBeatControl(Controller controller, Parameter p) {
		super(controller, p.getParent() + " - " + p.toString() + " Animator");
		parameter = (NumberParameter) p;

		requestBind();
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

}
