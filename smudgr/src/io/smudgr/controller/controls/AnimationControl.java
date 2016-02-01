package io.smudgr.controller.controls;

import io.smudgr.alg.param.NumberParameter;
import io.smudgr.alg.param.Parameter;
import io.smudgr.controller.Controller;

public class AnimationControl extends Controllable {

	private static final double[] SPEEDS = { 1 / 128.0, 1 / 96.0, 1 / 64.0, 1 / 46.0, 1 / 32.0, 1 / 24.0, 1 / 16.0, 1 / 12.0, 1 / 8.0, 1 / 4.0, 1 / 2.0, 1.0, 2.0, 4.0, 8.0 };

	private NumberParameter parameter;
	private boolean run = true;

	private int speed;

	public AnimationControl(Controller controller, Parameter p) {
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
		System.out.println(1 / SPEEDS[speed]);
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
