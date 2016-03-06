package io.smudgr.view.cef;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import io.smudgr.source.Frame;

public class RenderFrame extends JFrame {

	private JFrame parent;
	private Frame lastFrame;
	private int posX, posY;
	private int totalWidth, totalHeight;

	public RenderFrame(JFrame parent) {
		this.parent = parent;

		getRootPane().putClientProperty("Window.shadow", Boolean.FALSE);
		setAlwaysOnTop(true);
		setUndecorated(true);
		setBackground(Color.black);
		setFocusableWindowState(false);

		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				parent.requestFocus();
			}

			public void windowDeactivated(WindowEvent e) {
				if (e.getOppositeWindow() == parent)
					parent.requestFocus();
			}
		});
	}

	public void init() {
		setVisible(true);
		createBufferStrategy(2);
	}

	public void updateDimensions() {
		Insets inset = parent.getInsets();
		posX = parent.getX() + inset.left + 400;
		posY = inset.top + parent.getY() + 50;
		totalWidth = parent.getWidth() - 400;
		totalHeight = parent.getHeight() - inset.top - inset.bottom - 260;

		if (lastFrame == null) {
			setVisible(false);
			return;
		}

		int displayHeight = totalHeight;
		int displayWidth = totalWidth;

		int height = lastFrame.getHeight();
		int width = lastFrame.getWidth();

		boolean needsResize = (height > displayHeight || width > displayWidth) || (height < displayHeight && width < displayWidth);
		boolean byHeight = ((double) displayWidth / width) * height > displayHeight;
		if (needsResize) {
			if (byHeight) {
				width = (int) (width * ((double) displayHeight / height));
				height = displayHeight;
			} else {
				height = (int) (height * ((double) displayWidth / width));
				width = displayWidth;
			}
		}

		int x = displayWidth / 2 - width / 2;
		int y = displayHeight / 2 - height / 2;

		setLocation(posX + x, posY + y);
		setSize(width, height);

		createBufferStrategy(2);
	}

	public void drawFittedImage(Frame image) {
		if (!parent.isActive())
			setVisible(false);
		else if (image != null && !isVisible())
			setVisible(true);

		if (lastFrame == null || lastFrame.getWidth() != image.getWidth() || lastFrame.getHeight() != image.getHeight()) {
			lastFrame = image;
			updateDimensions();
		} else
			lastFrame = image;

		if (lastFrame == null)
			return;

		BufferStrategy st = getBufferStrategy();

		if (st == null) {
			createBufferStrategy(2);
			st = getBufferStrategy();
		}

		Graphics g = st.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.drawImage(image.getBufferedImage(), 0, 0, getWidth(), getHeight(), null);

		g.dispose();

		try {
			st.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final long serialVersionUID = -6876566152495162962L;

}
