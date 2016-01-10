package io.smudgr.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import io.smudgr.Smudge;
import io.smudgr.controller.Controller;
import io.smudgr.model.Frame;

public class JView implements View, Runnable, KeyListener {

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

		frame = smudge.render().fitToSize(displayWidth, displayHeight);

		int x = displayWidth / 2 - frame.getWidth() / 2;
		int y = displayHeight / 2 - frame.getHeight() / 2;

		Graphics g = strategy.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, window.getWidth(), window.getHeight());
		g.drawImage(frame.getImage(), x, y, null);
		g.dispose();

		strategy.show();
	}

	public void run() {
		while (!exit) {
			draw();
		}

		window.setVisible(false);
		strategy.getDrawGraphics().dispose();
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
