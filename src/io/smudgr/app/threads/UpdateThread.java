package io.smudgr.app.threads;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.Project;

/**
 * The {@link UpdateThread} is responsible for updating application logic at a
 * specific rate, typically relative to the current application instance
 * {@link Project#getBPM()}
 */
public class UpdateThread extends AppThread {

	/**
	 * Instantiate a new {@link UpdateThread}
	 */
	public UpdateThread() {
		super("Update Thread");
	}

	private long now, lastTickNs;

	/**
	 * Convert an amount of milliseconds to ideal application update cycles,
	 * using the current update rate.
	 * 
	 * @param ms
	 *            milliseconds
	 * @return The rounded amount of ideal application update cycles that
	 *         should happen in the given amount of milliseconds
	 * @see Controller#ticksToMs(int)
	 */
	public int msToTicks(int ms) {
		return ms / (1000 / getTarget());
	}

	@Override
	public void setPaused(boolean paused) {
		if (!paused)
			resetTimer();

		super.setPaused(paused);
	}

	@Override
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

	@Override
	protected void printStatus() {
		if (ticks != getTarget())
			System.out.println(ticks + " updates (should be " + getTarget() + ")");
	}

	@Override
	protected void slowdown() {
		while (now - lastTickNs < targetTickNs) {
			sleep(0, 50000);
			now = System.nanoTime();
		}
	}

	@Override
	protected void resetTimer() {
		super.resetTimer();
		now = System.nanoTime();
		lastTickNs = now;
	}

}
