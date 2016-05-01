package io.smudgr.app.threads;

import io.smudgr.app.Controller;

public class UpdateThread extends AppThread {

	public UpdateThread() {
		super("Update Thread");
	}

	private long now, lastTickNs;

	public int msToTicks(int ms) {
		return (int) (ms / (1000 / getTarget()));
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
			Thread.yield();

			try {
				Thread.sleep(1);
			} catch (Exception e) {
			}

			now = System.nanoTime();
		}
	}

}
