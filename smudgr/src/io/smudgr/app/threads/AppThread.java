package io.smudgr.app.threads;

public abstract class AppThread implements Runnable {

	private Thread thread;
	private long targetTickNs, lastTickNs;
	private volatile boolean running, paused, finished;

	public void start() {
		running = true;
		finished = false;

		thread = new Thread(this);
		thread.start();
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
		targetTickNs = 1000000000 / ticksPerSecond;
	}

	public long getTarget() {
		return 1000000000 / targetTickNs;
	}

	public void run() {
		lastTickNs = System.nanoTime();
		long timer = System.currentTimeMillis();
		int ticks = 0;

		while (running) {
			while (paused) {
				lastTickNs = System.nanoTime();

				if (!running)
					break;
			}

			try {
				execute();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}

			ticks++;

			if (System.currentTimeMillis() - timer >= 1000) {
				timer += 1000;
				printStatus(ticks);

				ticks = 0;
			}

			slowDown();

			lastTickNs = System.nanoTime();
		}

		finished = true;
	}

	protected abstract void execute();

	protected abstract void printStatus(int ticksPerSecond);

	protected void slowDown() {
		long diff = System.nanoTime() - lastTickNs;
		if (diff < targetTickNs) {
			try {
				diff = lastTickNs - System.nanoTime() + targetTickNs;
				long ms = (long) Math.floor(diff / 1000000.0);
				int ns = (int) (diff % 1000000);

				if (ms > 1)
					Thread.sleep(ms, Math.max(ns, 0));
			} catch (Exception e) {
			}
		}
	}

}
