package io.smudgr.app.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import io.smudgr.app.Controller;
import io.smudgr.project.smudge.util.Frame;

public class FullscreenView implements View, KeyListener {

	private int displayNumber = 0;
	private JFrame screen;
	private GraphicsDevice display;

	private BufferedImage nativeImage = null;

	public FullscreenView(int displayNumber) {
		this.displayNumber = displayNumber;
	}

	public void start() {
		try {
			display = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[displayNumber];

			screen = new JFrame("smudgr");

			screen.setBounds(display.getDefaultConfiguration().getBounds());
			screen.setExtendedState(JFrame.MAXIMIZED_BOTH);
			screen.setUndecorated(true);
			screen.addKeyListener(this);

			if (!System.getProperty("os.name").startsWith("Windows"))
				display.setFullScreenWindow(screen);
			screen.setVisible(true);

			screen.createBufferStrategy(2);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Unable to bind to monitor #: " + displayNumber);
		}
	}

	public void update(Frame frame) {
		if (nativeImage == null || screen.getWidth() != nativeImage.getWidth() || screen.getHeight() != nativeImage.getHeight())
			nativeImage = getNewNativeImage(screen.getWidth(), screen.getHeight());

		frame.drawTo(nativeImage);

		BufferStrategy st = screen.getBufferStrategy();

		Graphics g = st.getDrawGraphics();

		if (nativeImage != null)
			g.drawImage(nativeImage, 0, 0, screen.getWidth(), screen.getHeight(), null);
		else {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, screen.getWidth(), screen.getHeight());
		}

		g.dispose();
		st.show();
	}

	public void stop() {
		display.setFullScreenWindow(null);
		screen.setVisible(false);
		screen.dispose();
	}

	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
			Controller.getInstance().stop();
	}

	public void keyReleased(KeyEvent arg0) {

	}

	public void keyTyped(KeyEvent arg0) {

	}

}
