package io.smudgr.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import io.smudgr.controller.controls.Controllable;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.view.View;

public class Controller implements KeyListener {
	public static final int TARGET_FPS = 60;
	public static final int TICKS_PER_BEAT = 50;

	private Smudge smudge;
	private View view;

	private UpdateThread updater;
	private RenderThread renderer;
	private boolean started;
	private int beatsPerMinute = 120;

	private ArrayList<Controllable> controls = new ArrayList<Controllable>();

	public void start() {
		if (started) {
			updater.setPaused(false);
			return;
		}

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

		view.setSource(smudge);

		updater = new UpdateThread(this);
		renderer = new RenderThread(view);

		started = true;
		updater.start();
		renderer.start();
	}

	public void pause() {
		updater.setPaused(true);
	}

	public void stop() {
		if (!started)
			return;

		started = false;
		System.out.println("Stopping...");
		updater.stop();
		renderer.stop();

		if (smudge != null)
			smudge.dispose();
	}

	public void update() {
		if (!started)
			return;

		for (Controllable c : controls)
			c.update();

		smudge.update();
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

	public void setBPM(int bpm) {
		beatsPerMinute = bpm;
	}

	public int getBPM() {
		return beatsPerMinute;
	}

	public ArrayList<Controllable> getControls() {
		return controls;
	}

	public void addControl(Controllable c) {
		if (!controls.contains(c))
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
