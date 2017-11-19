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
import io.smudgr.util.PixelFrame;

/**
 * Instances of the {@link Window} class create an empty {@link JFrame} to flush
 * pixels to efficiently.
 */
public class Window extends JFrame implements KeyListener, WindowListener {

	private GraphicsDevice display = null;

	private BufferedImage nativeImage = null;
	private BufferStrategy bufferStrategy = null;
	private Graphics graphicsContext = null;

	private int lastFrameW, lastFrameH;

	/**
	 * Create a new non-fullscreen window
	 */
	public Window() {
		this(-1);
	}

	/**
	 * Create a new window in native fullscreen mode on the given display.
	 *
	 * @param fullscreenDisplay
	 *            ID of the monitor to use for fullscreen. -1 if fullscreen is
	 *            not desired on any monitor.
	 */
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

	/**
	 * Update this window with the given frame.
	 *
	 * @param frame
	 *            New frame.
	 */
	public void update(PixelFrame frame) {
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

	/**
	 * Dispose of this window's resources
	 */
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

	@Override
	public void windowClosing(WindowEvent e) {
		Controller.getInstance().stop();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
			Controller.getInstance().stop();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	private static final long serialVersionUID = 1L;

}
