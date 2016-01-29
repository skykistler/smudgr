package io.smudgr.controller;

public class UpdateThread implements Runnable {

	private Controller controller;
	private boolean running;

	public UpdateThread(Controller c) {
		controller = c;
	}

	public void start() {
		running = true;

		Thread t = new Thread(this);
		t.start();
	}

	public void stop() {
		running = false;
	}

	public void run() {
		final int nsInSecond = 1000000000;

		long lastSecond = System.currentTimeMillis();
		double lastUpdateTime = System.nanoTime();
		int updates = 0;

		while (running) {
			double beatsPerSecond = controller.getBPM() / 60.0;
			double TICKS_PER_SECOND = Controller.TICKS_PER_BEAT * beatsPerSecond;
			double TIME_BETWEEN_UPDATES = nsInSecond / TICKS_PER_SECOND;

			// Update enough to catch up
			double now = System.nanoTime();
			while (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
				controller.update();
				lastUpdateTime += TIME_BETWEEN_UPDATES;
				updates++;
			}

			if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
				lastUpdateTime = now - TIME_BETWEEN_UPDATES;
			}

			// Output ticks per second
			if (System.currentTimeMillis() - lastSecond >= 1000) {
				//				if (updates != TICKS_PER_SECOND)
				System.out.println(updates + " updates (should be " + TICKS_PER_SECOND + ")");
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
	}

}
