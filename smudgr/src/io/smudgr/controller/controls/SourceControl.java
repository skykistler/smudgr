package io.smudgr.controller.controls;

import io.smudgr.controller.BaseController;
import io.smudgr.smudge.source.Source;
import io.smudgr.smudge.source.SourceSet;

public class SourceControl extends Controllable {

	public String getName() {
		return "Source Switcher";
	}

	public SourceControl() {
		requestBind();
	}

	public void increment() {
		Source s = BaseController.getInstance().getSmudge().getSource();
		if (s instanceof SourceSet) {
			((SourceSet) s).increment();
		}
	}

	public void decrement() {
		Source s = BaseController.getInstance().getSmudge().getSource();
		if (s instanceof SourceSet) {
			((SourceSet) s).decrement();
		}
	}

}
