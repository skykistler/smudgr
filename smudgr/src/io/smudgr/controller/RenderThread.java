package io.smudgr.controller;

import io.smudgr.view.View;

public class RenderThread implements Runnable {

	private View view;
	private Thread thread;
	private volatile boolean running;
	private boolean finished;

	public RenderThread(View v) {
		view = v;
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

	public boolean isFinished() {
		return finished;
	}

	public void run() {
		long targetFrameNs = 1000000000 / Controller.TARGET_FPS;

		long lastFrame = System.nanoTime();
		long timer = System.currentTimeMillis();
		int frames = 0;

		while (running) {
			try {
				view.draw();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				System.out.println("Rendering stopped.");
				view.getController().stop();
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

		view.dispose();
		finished = true;
	}

}
