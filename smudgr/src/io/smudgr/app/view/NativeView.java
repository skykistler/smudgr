package io.smudgr.app.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import io.smudgr.app.Controller;
import io.smudgr.project.smudge.source.Frame;

public class NativeView implements View, KeyListener {

	private int displayNumber = 0;
	private JFrame fullscreenWindow;
	private GraphicsDevice display;

	private JFrame monitor;
	private boolean showMonitor;

	public NativeView() {
		this(0, false);
	}

	public NativeView(int displayNumber, boolean showMonitor) {
		this.displayNumber = displayNumber;
		this.showMonitor = showMonitor;
	}

	public void start() {
		if (displayNumber >= 0) {
			try {
				display = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[displayNumber];

				fullscreenWindow = new JFrame("smudgr");

				fullscreenWindow.setBounds(display.getDefaultConfiguration().getBounds());
				fullscreenWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
				fullscreenWindow.setUndecorated(true);
				fullscreenWindow.addKeyListener(this);

				if (!System.getProperty("os.name").startsWith("Windows"))
					display.setFullScreenWindow(fullscreenWindow);
				fullscreenWindow.setVisible(true);

				fullscreenWindow.createBufferStrategy(2);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Unable to bind to monitor #: " + displayNumber);
			}
		}

		if (showMonitor) {
			monitor = new JFrame("smudgr");
			monitor.setBounds(new Rectangle(800, 600));
			monitor.setVisible(true);
			monitor.createBufferStrategy(2);

			if (fullscreenWindow == null) {
				monitor.addKeyListener(this);
				monitor.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						Controller.getInstance().stop();
					}
				});
			}
		}
	}

	public void update(Frame frame) {
		if (fullscreenWindow != null)
			drawFittedImageToFrame(fullscreenWindow, frame.getBufferedImage());

		if (showMonitor)
			drawFittedImageToFrame(monitor, frame.getBufferedImage());
	}

	public void stop() {
		if (fullscreenWindow != null) {
			display.setFullScreenWindow(null);
			fullscreenWindow.setVisible(false);
			fullscreenWindow.dispose();
		}

		if (monitor != null) {
			monitor.setVisible(false);
			monitor.dispose();
		}
	}

	private void drawFittedImageToFrame(JFrame f, BufferedImage image) {
		int displayHeight = f.getHeight();
		int displayWidth = f.getWidth();

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

		BufferStrategy st = f.getBufferStrategy();

		Graphics g = st.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, displayWidth, displayHeight);

		if (image != null)
			g.drawImage(image, x, y, width, height, null);

		g.dispose();

		st.show();
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
