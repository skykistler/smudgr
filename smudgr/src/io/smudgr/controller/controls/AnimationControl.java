package io.smudgr.controller.controls;

import io.smudgr.alg.param.NumberParameter;
import io.smudgr.alg.param.Parameter;
import io.smudgr.controller.Controller;

public class AnimationControl extends Controllable {

	private Parameter parameter;
	private boolean run = true;

	private double initialStep, speed, increment;

	public AnimationControl(Controller controller, Parameter p) {
		this(controller, p, -1);
	}

	public AnimationControl(Controller controller, Parameter p, double increment) {
		super(controller, p.toString() + " Animator");
		parameter = p;

		if (parameter instanceof NumberParameter)
			initialStep = ((NumberParameter) parameter).getStep();

		if (increment < 0)
			increment = initialStep;

		speed = this.increment = increment;

		requestBind();
	}

	public void update() {
		if (run)
			parameter.increment();
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
		if (parameter instanceof NumberParameter) {

			speed += increment;

			NumberParameter p = (NumberParameter) parameter;
			p.setStep(initialStep + speed);
		}
	}

	public void decrement() {
		if (parameter instanceof NumberParameter) {
			speed -= increment;

			NumberParameter p = (NumberParameter) parameter;
			p.setStep(initialStep + speed);
		}
	}

}
