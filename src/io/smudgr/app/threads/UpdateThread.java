package io.smudgr.app.threads;

import io.smudgr.app.controller.Controller;

public class UpdateThread extends AppThread {

	public UpdateThread() {
		super("Update Thread");
	}

	private long now, lastTickNs;

	public int msToTicks(int ms) {
		return (int) (ms / (1000 / getTarget()));
	}

	public void setPaused(boolean paused) {
		if (!paused)
			resetTimer();

		super.setPaused(paused);
	}

	protected void execute() {
		now = System.nanoTime();

		if (lastTickNs == 0)
			lastTickNs = now;

		while (now - lastTickNs > targetTickNs) {
			update();
			lastTickNs += targetTickNs;
			ticks++;
		}
	}

	private void update() {
		Controller.getInstance().update();
	}

	protected void printStatus() {
		if (ticks != getTarget())
			System.out.println(ticks + " updates (should be " + getTarget() + ")");
	}

	protected void slowdown() {
		while (now - lastTickNs < targetTickNs) {
			sleep(0, 50000);
			now = System.nanoTime();
		}
	}

	protected void resetTimer() {
		super.resetTimer();
		now = System.nanoTime();
		lastTickNs = now;
	}

}
