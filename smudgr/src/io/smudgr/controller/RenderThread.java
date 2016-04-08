package io.smudgr.controller;

import io.smudgr.output.FrameOutput;

public class RenderThread implements Runnable {

	private Controller controller;
	private Thread thread;
	private long targetFrameNs, lastFrameNs;
	private volatile boolean running;
	private boolean finished;

	private FrameOutput output;
	private int everyXTicks;

	public RenderThread(Controller controller) {
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

	public void startOutput(FrameOutput output, int everyXTicks) {
		this.output = output;
		this.everyXTicks = everyXTicks;
	}

	public void stopOutput() {
		output = null;
	}

	public boolean isFinished() {
		return finished;
	}

	private void setTargetFPS(int fps) {
		targetFrameNs = 1000000000 / fps;
	}

	public void run() {
		lastFrameNs = System.nanoTime();
		long timer = System.currentTimeMillis();
		int frames = 0;

		while (running) {
			try {
				if (output != null) {
					for (int i = 0; i < everyXTicks; i++) {
						controller.update();
					}
				}

				controller.getSmudge().render();

				if (output != null)
					output.addFrame(controller.getSmudge().getFrame());

			} catch (IllegalStateException e) {
				e.printStackTrace();
				System.out.println("Rendering stopped.");
				controller.stop();
			}

			frames++;

			if (System.currentTimeMillis() - timer >= 1000) {
				timer += 1000;
				System.out.println(frames + " fps");

				frames = 0;
			}

			if (output == null)
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
