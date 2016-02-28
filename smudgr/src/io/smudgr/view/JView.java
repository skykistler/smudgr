package io.smudgr.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import io.smudgr.controller.Controller;
import io.smudgr.source.Frame;
import io.smudgr.source.Source;

public class JView implements View {
	private Controller controller;

	private int displayNumber = 0;
	private JFrame fullscreenWindow;
	private GraphicsDevice display;

	private JFrame monitor;
	private boolean showMonitor;

	private Source source;

	public JView(Controller controller) {
		this(controller, 0, false);
	}

	public JView(Controller controller, int displayNumber, boolean showMonitor) {
		this.controller = controller;
		this.displayNumber = displayNumber;
		this.showMonitor = showMonitor;

		controller.setView(this);
	}

	public void init() {
		if (displayNumber >= 0) {
			display = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[displayNumber];

			fullscreenWindow = new JFrame("smudgr");

			fullscreenWindow.setBounds(display.getDefaultConfiguration().getBounds());
			fullscreenWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
			fullscreenWindow.setUndecorated(true);
			fullscreenWindow.addKeyListener(controller);

			if (!System.getProperty("os.name").startsWith("Windows"))
				display.setFullScreenWindow(fullscreenWindow);
			fullscreenWindow.setVisible(true);

			fullscreenWindow.createBufferStrategy(2);
		}

		if (showMonitor) {
			monitor = new JFrame("smudgr");
			monitor.setBounds(new Rectangle(800, 600));
			monitor.setVisible(true);
			monitor.createBufferStrategy(2);

			if (fullscreenWindow == null)
				monitor.addKeyListener(controller);
		}
	}

	public void draw() {
		if (source == null)
			return;

		Frame frame = source.getFrame();

		if (frame == null)
			return;

		if (fullscreenWindow != null)
			drawFittedImageToFrame(fullscreenWindow, frame.getBufferedImage());

		if (showMonitor)
			drawFittedImageToFrame(monitor, frame.getBufferedImage());
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source s) {
		source = s;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller c) {
		controller = c;

		if (controller.getView() != this)
			controller.setView(this);
	}

	public void dispose() {
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

}
