package io.smudgr.app.threads;

import javax.swing.SwingUtilities;

import io.smudgr.app.Controller;
import io.smudgr.app.view.View;
import io.smudgr.project.smudge.Smudge;
import io.smudgr.project.smudge.source.Frame;

public class ViewThread implements Runnable {

	private View view;
	private Thread thread;
	private int currentFps;
	private long targetFrameNs;
	private volatile boolean running;
	private boolean finished;

	public ViewThread(View view) {
		this.view = view;

		setTargetFPS(Controller.TARGET_FPS);
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

	public int getCurrentFPS() {
		return currentFps;
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
				Smudge smudge = Controller.getInstance().getProject().getSmudge();
				if (smudge == null)
					continue;

				Frame frame = smudge.getFrame();
				if (frame == null)
					continue;

				view.update(frame);
			} catch (IllegalStateException e) {
				e.printStackTrace();
				System.out.println("View updating stopped.");
				running = false;
				break;
			}

			frames++;

			if (System.currentTimeMillis() - timer >= 1000) {
				timer += 1000;

				currentFps = frames;
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
				view.stop();
			}
		});

		finished = true;
	}

}
