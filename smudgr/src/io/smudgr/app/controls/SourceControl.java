package io.smudgr.app.controls;

import io.smudgr.app.Controller;
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
		Source s = Controller.getInstance().getSmudge().getSource();
		if (s instanceof SourceSet) {
			((SourceSet) s).increment();
		}
	}

	public void decrement() {
		Source s = Controller.getInstance().getSmudge().getSource();
		if (s instanceof SourceSet) {
			((SourceSet) s).decrement();
		}
	}

}
