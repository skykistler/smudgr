package io.smudgr.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import io.smudgr.Smudge;
import io.smudgr.controller.controls.Controllable;
import io.smudgr.view.View;

public abstract class Controller implements KeyListener {
	public static final int TARGET_FPS = 60;
	public static final int TARGET_UPDATES = 100;

	private Smudge smudge;
	private View view;

	private UpdateThread updater;
	private RenderThread renderer;

	private ArrayList<Controllable> controls = new ArrayList<Controllable>();

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

		updater = new UpdateThread(this);
		renderer = new RenderThread(view);

		updater.start();
		renderer.start();
	}

	public void stop() {
		updater.stop();
		renderer.stop();

		while (!updater.isFinished() || !renderer.isFinished())
			;

		view.stop();
		System.exit(0);
	}

	public void update() {
		for (Controllable c : controls)
			c.update();
	}

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

	public ArrayList<Controllable> getControls() {
		return controls;
	}

	public void addControl(Controllable c) {
		controls.add(c);
	}

	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
			stop();
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

}
