package io.smudgr.controller;

import io.smudgr.view.View;

public class RenderThread implements Runnable {

	private View view;
	private volatile boolean running;
	private boolean finished;

	public RenderThread(View v) {
		view = v;
	}

	public void start() {
		running = true;
		Thread t = new Thread(this);
		t.start();
	}

	public void stop() {
		running = false;
	}

	public boolean isFinished() {
		return finished;
	}

	public void run() {
		long targetFrameNs = 1000000000 / SmudgeController.TARGET_FPS;

		long lastFrame = System.nanoTime();
		long timer = System.currentTimeMillis();
		int frames = 0;

		while (running) {
			try {
				view.draw();
			} catch (IllegalStateException e) {
				System.out.println("Rendering stopped.");
				break;
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
					Thread.sleep(ms, ns);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			lastFrame = System.nanoTime();
		}

		finished = true;
		view.dispose();
	}

}
