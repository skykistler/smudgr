package io.smudgr.controller.controls;

import io.smudgr.alg.param.NumberParameter;
import io.smudgr.alg.param.Parameter;
import io.smudgr.controller.Controller;

public class AnimationControl extends Controllable {

	private Parameter parameter;
	private boolean run = true;

	private double initialStep, divider = 1;

	public AnimationControl(Controller controller, Parameter p) {
		super(controller, p.toString() + " Animator");
		parameter = p;

		if (parameter instanceof NumberParameter)
			initialStep = ((NumberParameter) parameter).getStep();

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

			NumberParameter p = (NumberParameter) parameter;

			double curStep = p.getStep();
			if (curStep < initialStep) {
				p.setStep(initialStep / --divider);
			} else
				p.setStep(curStep + initialStep);
		}
	}

	public void decrement() {
		if (parameter instanceof NumberParameter) {
			NumberParameter p = (NumberParameter) parameter;

			double curStep = p.getStep();
			if (curStep <= initialStep) {
				p.setStep(initialStep / ++divider);
			} else
				p.setStep(curStep - initialStep);
		}
	}

}
