package io.smudgr.extensions.cef.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import io.smudgr.app.view.View;
import io.smudgr.project.smudge.source.Frame;

public class RenderFrame extends JFrame implements View {

	private static RenderFrame instance;

	public static RenderFrame getInstance() {
		return instance;
	}

	private JFrame parent;
	private Frame currentFrame;
	private int offsetX, offsetY;
	private int viewWidth, viewHeight;

	public RenderFrame(JFrame parent) {
		instance = this;

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

	public void start() {
		setVisible(true);
		createBufferStrategy(2);
	}

	public void update(Frame frame) {
		Frame lastFrame = currentFrame;

		currentFrame = frame;

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

		g.drawImage(frame.getBufferedImage(), 0, 0, getWidth(), getHeight(), null);

		g.dispose();

		try {
			st.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		dispose();
	}

	public void updateIsVisible() {
		boolean visible = parent.isActive() && currentFrame != null;
		if (isVisible() != visible)
			setVisible(visible);
	}

	public void updateDimensions() {
		updateIsVisible();
		if (!isVisible())
			return;

		Insets inset = parent.getInsets();
		int x = inset.left + parent.getX() + offsetX;
		int y = inset.top + parent.getY() + offsetY;

		int height = currentFrame.getHeight();
		int width = currentFrame.getWidth();

		boolean needsResize = (height > viewHeight || width > viewWidth) || (height < viewHeight && width < viewWidth);

		if (needsResize) {
			if (height > viewHeight || width > viewWidth) {
				width = (int) (width * ((double) viewHeight / height));
				height = viewHeight;
			} else if (height < viewHeight && width < viewWidth) {
				width = (int) (width * ((double) viewHeight / height));
				height = viewHeight;
			}

			setSize(width, height);

			createBufferStrategy(2);
		}

		x += viewWidth / 2 - width / 2;
		y += viewHeight / 2 - height / 2;

		setLocation(x, y);
	}

	public void setX(String offsetX) {
		this.offsetX = Integer.parseInt(offsetX);
	}

	public void setY(String offsetY) {
		this.offsetY = Integer.parseInt(offsetY);
	}

	public void setWidth(String renderViewWidth) {
		viewWidth = Integer.parseInt(renderViewWidth);
	}

	public void setHeight(String renderViewHeight) {
		viewHeight = Integer.parseInt(renderViewHeight);
	}

	private static final long serialVersionUID = -6876566152495162962L;

}
