package io.smudgr.app.threads;

import io.smudgr.app.Controller;
import io.smudgr.app.output.FrameOutput;

public class RenderThread implements Runnable {

	private Thread thread;
	private long targetFrameNs, lastFrameNs;
	private volatile boolean running, paused, finished;

	private FrameOutput output;
	private int everyXTicks;

	public RenderThread() {
		setTargetFPS(Controller.TARGET_FPS);
	}

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

	public void startOutput(FrameOutput output, int everyXTicks) {
		this.output = output;
		this.everyXTicks = everyXTicks;
	}

	public void stopOutput() {
		output = null;
		everyXTicks = 0;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setTargetFPS(int fps) {
		targetFrameNs = 1000000000 / fps;
	}

	public void run() {
		lastFrameNs = System.nanoTime();
		long timer = System.currentTimeMillis();
		int frames = 0;

		while (running) {
			while (paused) {
				lastFrameNs = System.nanoTime();

				if (!running)
					break;
			}

			try {
				if (output != null)
					for (int i = 0; i <= everyXTicks; i++)
						Controller.getInstance().update();

				synchronized (Controller.getInstance()) {
					Controller.getInstance().getProject().getSmudge().render();
				}

				if (output != null)
					output.addFrame(Controller.getInstance().getProject().getSmudge().getFrame());

			} catch (IllegalStateException e) {
				e.printStackTrace();
				System.out.println("Rendering stopped.");
			}

			frames++;

			if (System.currentTimeMillis() - timer >= 1000) {
				timer += 1000;
				System.out.println(frames + " fps");

				frames = 0;
			}

			enforceFrameRate();

			lastFrameNs = System.nanoTime();
		}

		finished = true;
	}

	public void enforceFrameRate() {
		long diff = System.nanoTime() - lastFrameNs;
		if (diff < targetFrameNs) {
			try {
				diff = lastFrameNs - System.nanoTime() + targetFrameNs;
				long ms = (long) Math.floor(diff / 1000000.0);
				int ns = (int) (diff % 1000000);
				Thread.sleep(Math.max(ms, 0), Math.max(ns, 0));
			} catch (Exception e) {
			}
		}
	}

}
