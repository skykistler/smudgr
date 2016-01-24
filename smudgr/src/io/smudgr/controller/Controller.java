package io.smudgr.controller;

import java.util.ArrayList;

import io.smudgr.Smudge;
import io.smudgr.controller.controls.Controllable;
import io.smudgr.view.View;

public abstract class Controller {
	public static final int TARGET_FPS = 60;

	private Smudge smudge;
	private View view;

	private UpdateThread updater;
	private int targetUpdatesPerSecond = 100;

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

		(updater = new UpdateThread()).start();
	}

	public boolean isRunning() {
		return updater.running;
	}

	public void stop() {
		updater.running = false;
	}

	public void update() {
		for (Controllable c : controls)
			c.update();
	}

	public ArrayList<Controllable> getControls() {
		return controls;
	}

	public void addControl(Controllable c) {
		controls.add(c);
	}

	private class UpdateThread implements Runnable {

		private boolean running;

		public void start() {
			running = true;

			Thread t = new Thread(this);
			t.start();
		}

		public void run() {
			final double TIME_BETWEEN_UPDATES = 1000000000 / targetUpdatesPerSecond;

			long lastSecond = System.currentTimeMillis();
			double lastUpdateTime = System.nanoTime();
			int updates = 0;

			while (running) {
				// Update enough to catch up
				double now = System.nanoTime();
				while (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
					update();
					lastUpdateTime += TIME_BETWEEN_UPDATES;
					updates++;
				}

				if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
					lastUpdateTime = now - TIME_BETWEEN_UPDATES;
				}

				// Output ticks per second
				if (System.currentTimeMillis() - lastSecond >= 1000) {
					if (updates != targetUpdatesPerSecond && (updates - 1) != targetUpdatesPerSecond)
						System.out.println(updates + " updates (should be " + targetUpdatesPerSecond + ")");
					updates = 0;
					lastSecond = System.currentTimeMillis();
				}

				// Sleep enough to slow down
				while (now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
					Thread.yield();

					try {
						Thread.sleep(1);
					} catch (Exception e) {
					}

					now = System.nanoTime();
				}
			}

			System.exit(0);
		}

	}
}
