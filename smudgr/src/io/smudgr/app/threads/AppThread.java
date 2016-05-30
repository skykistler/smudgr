package io.smudgr.app.threads;

public abstract class AppThread implements Runnable {

	private String threadName;
	private Thread thread;
	protected long targetTickNs, preTickNs, ticks;
	private volatile boolean running, paused, finished;

	public AppThread(String name) {
		threadName = name;
	}

	public void start() {
		running = true;
		finished = false;

		thread = new Thread(this);
		thread.start();

		thread.setName(threadName);
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public void stop() {
		running = false;
		thread.interrupt();
	}

	public boolean isFinished() {
		return finished;
	}

	public void setTarget(int ticksPerSecond) {
		targetTickNs = (long) Math.floor(1000000000.0 / ticksPerSecond);
	}

	public int getTarget() {
		return (int) Math.floor(1000000000.0 / targetTickNs);
	}

	public void run() {
		long timer = System.currentTimeMillis();

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

			if (System.currentTimeMillis() - timer >= 1000) {
				timer = System.currentTimeMillis();
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

	protected void onStop() {
		System.out.println("A smudgr process stopped");
	}

}
