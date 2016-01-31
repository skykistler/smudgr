package io.smudgr.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import io.smudgr.Smudge;
import io.smudgr.controller.Controller;
import io.smudgr.source.Frame;

public class JView implements View {
	private Controller controller;

	private int display = 0;
	private int displayWidth;
	private int displayHeight;
	private JFrame window;
	private BufferStrategy strategy;
	private GraphicsDevice monitor;

	private Frame frame;

	public JView(Controller controller) {
		this(controller, 0);
	}

	public JView(Controller controller, int display) {
		this.controller = controller;
		this.display = display;

		controller.setView(this);
	}

	public void init() {
		monitor = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[display];
		displayWidth = monitor.getDisplayMode().getWidth();
		displayHeight = monitor.getDisplayMode().getHeight();

		window = new JFrame("smudgr");

		window.setBounds(monitor.getDefaultConfiguration().getBounds());
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setUndecorated(true);
		window.addKeyListener(controller);

		if (!System.getProperty("os.name").startsWith("Windows"))
			monitor.setFullScreenWindow(window);
		window.setVisible(true);

		window.createBufferStrategy(2);
		strategy = window.getBufferStrategy();
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

	public void dispose() {
		monitor.setFullScreenWindow(null);
		window.dispose();
	}

	private void drawFittedImage(Graphics g, BufferedImage image) {
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

}
