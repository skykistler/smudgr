package io.smudgr.controller;

public class UpdateThread implements Runnable {

	private Controller controller;
	private Thread thread;
	private double ticksPerSecond;
	private boolean running, paused, finished;

	public UpdateThread(Controller c) {
		controller = c;
	}

	public void start() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public void setPaused(boolean p) {
		paused = p;
	}

	public void stop() {
		running = false;
		thread.interrupt();
	}

	public double getTicksPerSecond() {
		return ticksPerSecond;
	}

	public int msToTicks(int ms) {
		return (int) (ms / (1000 / ticksPerSecond));
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
				System.out.println(paused);

				if (!running)
					break;
			}

			double beatsPerSecond = controller.getBPM() / 60.0;
			ticksPerSecond = Math.ceil(Controller.TICKS_PER_BEAT * beatsPerSecond);
			double timeForUpdate = nsInSecond / ticksPerSecond;

			// Update enough to catch up
			double now = System.nanoTime();
			while (now - lastUpdateTime >= timeForUpdate) {
				try {
					synchronized (controller.getSmudge()) {
						controller.update();
					}
				} catch (Exception e) {
					e.printStackTrace();
					controller.stop();
				}
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

				if (!running)
					break;
			}
		}

		finished = true;
	}

}
