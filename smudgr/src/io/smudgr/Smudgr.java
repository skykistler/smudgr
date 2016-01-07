package io.smudgr;

import java.util.ArrayList;

import io.smudgr.controller.Controller;
import io.smudgr.view.PView;
import io.smudgr.view.View;

public class Smudgr {
	public static void startSmudge(Smudge s) {
		Smudgr r = new Smudgr();
		r.addSmudge(s);
		r.start();
	}

	public ArrayList<Smudge> smudges = new ArrayList<Smudge>();

	private Controller controller;
	private View view;

	public Smudgr() {
		controller = new Controller();
		view = new PView(controller);

		controller.setView(view);
	}

	public void start() {
		controller.start();
	}

	public void addSmudge(Smudge s) {
		controller.setSmudge(s);
	}
}
