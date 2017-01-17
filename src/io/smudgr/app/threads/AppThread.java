package io.smudgr.app.threads;

/**
 * The abstract {@link AppThread} class defines generic behavior for an
 * ideally-timed loop thread.
 * <p>
 * {@link AppThread} implementations can adjust their own ideal count of
 * executions per second.
 */
public abstract class AppThread implements Runnable {

	private String threadName;
	private Thread thread;
	protected long targetTickNs, preTickNs, ticks, timerMs;
	private volatile boolean running, paused, finished;

	/**
	 * Instantiate a new {@link AppThread} with the given name.
	 * 
	 * @param name
	 *            {@link String}
	 */
	public AppThread(String name) {
		threadName = name;
	}

	/**
	 * Start execution of the thread loop.
	 * 
	 * @see AppThread#setPaused(boolean)
	 * @see AppThread#stop()
	 */
	public void start() {
		running = true;
		finished = false;

		thread = new Thread(this);
		thread.start();

		thread.setName(threadName);
	}

	/**
	 * Set the paused state of the {@link AppThread}
	 * 
	 * @param paused
	 *            {@code boolean}
	 * @see AppThread#stop()
	 */
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	/**
	 * Completely halt execution of the thread.
	 * 
	 * @see AppThread#setPaused(boolean)
	 */
	public void stop() {
		running = false;
		thread.interrupt();
	}

	/**
	 * Gets whether this thread has been permanently halted.
	 * 
	 * @return {@code true} if thread has permanently halted.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Sets the target amount of executions per second.
	 * 
	 * @param ticksPerSecond
	 *            {@code int}
	 */
	public void setTarget(int ticksPerSecond) {
		targetTickNs = (long) Math.floor(1000000000.0 / ticksPerSecond);
	}

	/**
	 * Gets the target amount of executions per second.
	 * 
	 * @return target amount of executions per second
	 */
	public int getTarget() {
		return (int) Math.floor(1000000000.0 / targetTickNs);
	}

	@Override
	public void run() {
		timerMs = System.currentTimeMillis();

		long prevTicks = 0;
		ticks = 0;

		while (running) {

			while (paused)
				if (!running)
					break;

			preTickNs = System.nanoTime();

			prevTicks = ticks;
			try {
				execute();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Only increment if execute() didn't increment for us
			if (prevTicks == ticks)
				ticks++;

			if (System.currentTimeMillis() - timerMs >= 1000) {
				timerMs = System.currentTimeMillis();
				printStatus();

				ticks = 0;
			}

			slowdown();
		}

		onStop();

		finished = true;
	}

	protected abstract void execute();

	protected abstract void printStatus();

	protected void slowdown() {
		long diff = System.nanoTime() - preTickNs;
		if (diff < targetTickNs) {
			diff = targetTickNs - diff;
			long ms = (long) Math.floor(diff / 1000000.0) - 1;
			int ns = (int) (diff % 1000000);

			sleep(ms, ns);
		}
	}

	protected void sleep(long ms, int ns) {
		try {
			Thread.sleep(ms, ns);
		} catch (Exception e) {
		}
	}

	protected void resetTimer() {
		timerMs = System.currentTimeMillis();
		ticks = 0;
	}

	protected void onStop() {
		System.out.println("A smudgr process stopped");
	}

}
