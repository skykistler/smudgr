package io.smudgr.controller.controls;

import io.smudgr.alg.param.Parameter;
import io.smudgr.controller.Controller;

public class AnimationControl extends Controllable {

	private Parameter parameter;
	private boolean run = true;
	private int wait = 1;
	private int loop = 0;
	private int counter = 0;

	public AnimationControl(Controller controller, Parameter p) {
		super(controller, p.toString() + " Animator");
		parameter = p;

		requestBind();
	}

	public void update() {
		if (run) {
			if (wait < 1) {
				for (int i = 0; i < loop; i++) {
					parameter.increment();
				}
			} else if (counter % wait == 0)
				parameter.increment();

			counter++;
		}
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
		wait--;
		if (wait < 1)
			loop++;
	}

	public void decrement() {
		wait++;
		if (wait < 1)
			loop--;
	}

}
