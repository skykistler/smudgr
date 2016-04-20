package io.smudgr.app.threads;

public abstract class AppThread implements Runnable {

	private Thread thread;
	protected long targetTickNs, lastTickNs, ticks;
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
		targetTickNs = (long) Math.floor(1000000000.0 / ticksPerSecond);
	}

	public int getTarget() {
		return (int) Math.floor(1000000000.0 / targetTickNs);
	}

	public void run() {
		lastTickNs = System.nanoTime();
		long timer = System.currentTimeMillis();
		ticks = 0;

		while (running) {

			while (paused) {
				lastTickNs = System.nanoTime();

				if (!running)
					break;
			}

			lastTickNs = System.nanoTime();

			try {
				execute();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}

			enforce();

			ticks++;

			if (System.currentTimeMillis() - timer >= 1000) {
				timer += 1000;
				printStatus();

				ticks = 0;
			}

		}

		onStop();

		finished = true;
	}

	protected abstract void execute();

	protected abstract void printStatus();

	protected void enforce() {
		long diff = System.nanoTime() - lastTickNs;
		if (diff < targetTickNs) {
			try {
				diff = targetTickNs - diff;
				long ms = (long) Math.floor(diff / 1000000.0) - 1;
				int ns = (int) (diff % 1000000);

				if (ms > 0)
					Thread.sleep(ms, ns);
			} catch (Exception e) {
			}
		}

		//		while (diff < targetTickNs) {
		//			//			Thread.yield();
		//
		//			try {
		//				Thread.sleep(0, 100);
		//			} catch (Exception e) {
		//			}
		//
		//			diff = System.nanoTime() - lastTickNs;
		//
		//			if (!running)
		//				break;
		//		}
	}

	protected void onStop() {
		System.out.println("A smudgr process stopped");
	}

}
