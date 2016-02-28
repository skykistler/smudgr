package io.smudgr.controller.controls;

import io.smudgr.controller.Controller;
import io.smudgr.source.smudge.param.NumberParameter;
import io.smudgr.source.smudge.param.Parameter;

public class AnimateByStepControl extends Controllable {

	private NumberParameter parameter;
	private boolean run = true;

	private double increment, speed;

	public AnimateByStepControl(Controller controller, Parameter p) {
		this(controller, p, .25);
	}

	public AnimateByStepControl(Controller controller, Parameter p, double increment) {
		super(controller, p.toString() + " Animator");
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

}
