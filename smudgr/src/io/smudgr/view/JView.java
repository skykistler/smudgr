package io.smudgr.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import io.smudgr.Smudge;
import io.smudgr.controller.Controller;
import io.smudgr.model.Frame;

public class JView implements View, Runnable, KeyListener {
	private final int targetFPS = 60;
	private final boolean showFPS = true;

	private Controller controller;

	private int displayWidth;
	private int displayHeight;
	private JFrame window;
	private BufferStrategy strategy;
	private boolean exit = false;

	private Frame frame;

	public JView(Controller controller) {
		this.controller = controller;

		controller.setView(this);
	}

	public void init() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		displayWidth = gd.getDisplayMode().getWidth();
		displayHeight = gd.getDisplayMode().getHeight();

		window = new JFrame("smudgr");

		window.setSize(displayWidth, displayHeight);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setUndecorated(true);
		window.addKeyListener(this);

		window.setVisible(true);

		window.createBufferStrategy(2);
		strategy = window.getBufferStrategy();

		Thread renderThread = new Thread(this);
		renderThread.start();
	}

	public void draw() {
		Smudge smudge = controller.getSmudge();

		Graphics g = strategy.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, window.getWidth(), window.getHeight());

		frame = smudge.render().copy();
		drawFittedImage(g, frame.getBufferedImage());

		g.dispose();

		strategy.show();
	}

	public void drawFittedImage(Graphics g, BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();

		if (height > displayHeight || width > displayWidth) {
			width = (int) (width * ((double) displayHeight / height));
			height = displayHeight;
		} else if (height < displayHeight && width < displayWidth) {
			width = (int) (width * ((double) displayHeight / height));
			height = displayHeight;
		}

		int x = displayWidth / 2 - width / 2;
		int y = displayHeight / 2 - height / 2;

		g.drawImage(image, x, y, width, height, null);
	}

	public void run() {
		long targetFrameNs = 1000000000l / targetFPS;
		long lastSecond = System.nanoTime();
		int frameCount = 0;

		while (!exit) {
			long frameStart = System.nanoTime();

			draw();

			frameCount++;

			if (System.nanoTime() - lastSecond > 1000000000) {
				if (showFPS)
					System.out.println(frameCount + "fps");

				lastSecond = System.nanoTime();
				frameCount = 0;
			}

			long diff = System.nanoTime() - frameStart;
			if (diff < targetFrameNs) {
				try {
					diff = frameStart - System.nanoTime() + targetFrameNs;
					long ms = diff / 1000000;
					int ns = (int) (diff % 1000000);
					Thread.sleep(ms, ns);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		window.setVisible(false);
		window.dispose();

		System.exit(0);
	}

	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
			exit = true;
	}

	public void keyReleased(KeyEvent arg0) {

	}

	public void keyTyped(KeyEvent arg0) {

	}

}
