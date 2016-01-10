package io.smudgr.controller;

import java.util.ArrayList;

import io.smudgr.Smudge;
import io.smudgr.controller.controls.Controllable;
import io.smudgr.view.View;

public abstract class Controller {

	private Smudge smudge;
	private View view;

	private ArrayList<Controllable> controls = new ArrayList<Controllable>();

	public Smudge getSmudge() {
		return smudge;
	}

	public void setSmudge(Smudge s) {
		smudge = s;

		if (smudge.getController() != this)
			s.setController(this);
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public void start() {
		if (smudge == null) {
			System.out.println("Smudge not set... can not start");
			return;
		}
		if (view == null) {
			System.out.println("View not set... can not start");
			return;
		}

		System.out.println("Setting up controls...");
		for (Controllable c : controls)
			c.init();

		smudge.init();
		view.init();
	}

	public ArrayList<Controllable> getControls() {
		return controls;
	}

	public void addControl(Controllable c) {
		controls.add(c);
	}

}
