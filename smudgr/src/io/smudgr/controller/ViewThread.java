package io.smudgr.controller;

import javax.swing.SwingUtilities;

public class ViewThread implements Runnable {

	private Controller controller;
	private Thread thread;
	private long targetFrameNs;
	private volatile boolean running;
	private boolean finished;

	public ViewThread(Controller controller) {
		this.controller = controller;

		setTargetFPS(BaseController.TARGET_FPS);
	}

	public void start() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		running = false;
		thread.interrupt();
	}

	public void setTargetFPS(int fps) {
		targetFrameNs = 1000000000 / fps;
	}

	public boolean isFinished() {
		return finished;
	}

	public void run() {
		long lastFrame = System.nanoTime();
		long timer = System.currentTimeMillis();
		int frames = 0;

		while (running) {
			try {
				controller.getView().update();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				System.out.println("View updating stopped.");
				controller.stop();
			}

			frames++;

			if (System.currentTimeMillis() - timer >= 1000) {
				timer += 1000;
				System.out.println(frames + " fps");

				frames = 0;
			}

			long diff = System.nanoTime() - lastFrame;
			if (diff < targetFrameNs) {
				try {
					diff = lastFrame - System.nanoTime() + targetFrameNs;
					long ms = (long) Math.floor(diff / 1000000.0);
					int ns = (int) (diff % 1000000);
					Thread.sleep(Math.max(ms, 0), Math.max(ns, 0));
				} catch (Exception e) {
				}
			}

			lastFrame = System.nanoTime();
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				controller.getView().stop();
			}
		});

		finished = true;
	}

}
