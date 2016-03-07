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
	private Frame currentFrame;
	private int offsetX, offsetY;
	private int viewWidth, viewHeight;

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

	public void draw(Frame image) {
		Frame lastFrame = currentFrame;

		currentFrame = image;

		updateIsVisible();
		if (!isVisible())
			return;

		if (lastFrame == null || lastFrame.getWidth() != currentFrame.getWidth() || lastFrame.getHeight() != currentFrame.getHeight())
			updateDimensions();

		BufferStrategy st = getBufferStrategy();

		if (st == null)
			return;

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

	public void updateIsVisible() {
		boolean visible = parent.isActive() && currentFrame != null;
		if (isVisible() != visible)
			setVisible(visible);
	}

	public void updateDimensions() {
		if (!isVisible())
			return;

		Insets inset = parent.getInsets();
		int x = inset.left + parent.getX() + offsetX;
		int y = inset.top + parent.getY() + offsetY;

		int height = currentFrame.getHeight();
		int width = currentFrame.getWidth();

		boolean needsResize = (height > viewHeight || width > viewWidth) || (height < viewHeight && width < viewWidth);
		boolean byHeight = (height * ((double) viewWidth / width)) > viewHeight;
		if (needsResize) {
			if (byHeight) {
				width = (int) (width * ((double) viewHeight / height));
				height = viewHeight;
			} else {
				height = (int) (height * ((double) viewWidth / width));
				width = viewWidth;
			}

			setSize(width, height);

			createBufferStrategy(2);
		}

		x += viewWidth / 2 - width / 2;
		y += viewHeight / 2 - height / 2;

		setLocation(x, y);
	}

	public void setX(int offsetX) {
		this.offsetX = offsetX;
	}

	public void setY(int offsetY) {
		this.offsetY = offsetY;
	}

	public void setWidth(int renderViewWidth) {
		viewWidth = renderViewWidth;
	}

	public void setHeight(int renderViewHeight) {
		viewHeight = renderViewHeight;
	}

	public boolean isVisible() {
		return currentFrame != null && super.isVisible();
	}

	private static final long serialVersionUID = -6876566152495162962L;

}
