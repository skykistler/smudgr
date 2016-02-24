package io.smudgr.controller;

public class UpdateThread implements Runnable {

	private SmudgeController controller;
	private volatile boolean running, paused;
	private boolean finished;

	public UpdateThread(SmudgeController c) {
		controller = c;
	}

	public void start() {
		running = true;

		Thread t = new Thread(this);
		t.start();
	}

	public void setPaused(boolean p) {
		paused = p;
	}

	public void stop() {
		running = false;
	}

	public boolean isFinished() {
		return finished;
	}

	public void run() {
		final int nsInSecond = 1000000000;

		long lastSecond = System.currentTimeMillis();
		double lastUpdateTime = System.nanoTime();
		int updates = 0;

		while (running) {
			while (paused) {
				lastUpdateTime = System.nanoTime();

				if (!running)
					break;
			}

			double beatsPerSecond = controller.getBPM() / 60.0;
			double ticksPerSecond = Math.ceil(SmudgeController.TICKS_PER_BEAT * beatsPerSecond);
			double timeForUpdate = nsInSecond / ticksPerSecond;

			// Update enough to catch up
			double now = System.nanoTime();
			while (now - lastUpdateTime > timeForUpdate) {
				controller.update();
				lastUpdateTime += timeForUpdate;
				updates++;
			}

			// Output ticks per second
			if (System.currentTimeMillis() - lastSecond >= 1000) {
				if (updates != ticksPerSecond)
					System.out.println(updates + " updates (should be " + ticksPerSecond + ")");
				updates = 0;
				lastSecond = System.currentTimeMillis();
			}

			// Sleep enough to slow down
			while (now - lastUpdateTime < timeForUpdate) {
				Thread.yield();

				try {
					Thread.sleep(1);
				} catch (Exception e) {
				}

				now = System.nanoTime();
			}
		}

		finished = true;
	}

}
