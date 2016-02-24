package io.smudgr.controller.controls;

import io.smudgr.controller.SmudgeController;
import io.smudgr.source.smudge.alg.param.NumberParameter;
import io.smudgr.source.smudge.alg.param.Parameter;

public class AnimateByStepControl extends Controllable {

	private NumberParameter parameter;
	private boolean run = true;

	private double increment, speed;

	public AnimateByStepControl(SmudgeController controller, Parameter p) {
		this(controller, p, .25);
	}

	public AnimateByStepControl(SmudgeController controller, Parameter p, double increment) {
		super(controller, p.getParent() + " - " + p.toString() + " Animator");
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
