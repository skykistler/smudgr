package io.smudgr.app.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import io.smudgr.app.controller.Controller;
import io.smudgr.util.Frame;

public class Window extends JFrame implements KeyListener, WindowListener {

	private GraphicsDevice display = null;

	private BufferedImage nativeImage = null;
	private BufferStrategy bufferStrategy = null;
	private Graphics graphicsContext = null;

	private int lastFrameW, lastFrameH;

	public Window() {
		this(-1);
	}

	public Window(int fullscreenDisplay) {
		super("smudgr");

		if (fullscreenDisplay != -1)
			makeFullscreen(fullscreenDisplay);

		setBackground(Color.BLACK);
		setVisible(true);

		addKeyListener(this);
		addWindowListener(this);

		createBufferStrategy(2);
	}

	private void makeFullscreen(int displayNumber) {
		try {
			display = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[displayNumber];

			setBounds(display.getDefaultConfiguration().getBounds());
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			setUndecorated(true);
			addKeyListener(this);

			if (!System.getProperty("os.name").startsWith("Windows"))
				display.setFullScreenWindow(this);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Unable to bind to monitor #: " + displayNumber);
		}
	}

	public void update(Frame frame) {
		boolean viewChanged = nativeImage == null || getWidth() != nativeImage.getWidth() || getHeight() != nativeImage.getHeight();
		boolean needsRefresh = bufferStrategy == null || bufferStrategy.contentsLost();
		boolean needsClear = lastFrameW != frame.getWidth() || lastFrameH != frame.getHeight();

		if (viewChanged || needsRefresh) {
			makeNewNativeImage(getWidth(), getHeight());

			if (graphicsContext != null)
				graphicsContext.dispose();

			bufferStrategy = getBufferStrategy();
			graphicsContext = bufferStrategy.getDrawGraphics();

			needsClear = true;
		}

		if (needsClear) {
			Graphics g = nativeImage.getGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, nativeImage.getWidth(), nativeImage.getHeight());
			g.dispose();

			graphicsContext.setColor(Color.BLACK);
			graphicsContext.fillRect(0, 0, getWidth(), getHeight());
		}

		frame.drawTo(nativeImage);

		graphicsContext.drawImage(nativeImage, 0, 0, getWidth(), getHeight(), null);
		bufferStrategy.show();
	}

	public void stop() {
		if (display != null)
			display.setFullScreenWindow(null);

		setVisible(false);
		dispose();

		if (graphicsContext != null)
			graphicsContext.dispose();
	}

	protected void makeNewNativeImage(int width, int height) {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = env.getDefaultScreenDevice();
		GraphicsConfiguration config = device.getDefaultConfiguration();

		nativeImage = config.createCompatibleImage(width, height, Transparency.OPAQUE);
	}

	public void windowClosing(WindowEvent e) {
		Controller.getInstance().stop();
	}

	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
			Controller.getInstance().stop();
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {

	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	private static final long serialVersionUID = 1L;

}
