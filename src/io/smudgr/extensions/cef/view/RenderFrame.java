package io.smudgr.extensions.cef.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import io.smudgr.app.view.Window;
import io.smudgr.util.Frame;

/**
 * Deprecated hardware accelerated window meant to sit on top of the CEF
 * instance and render smudgr.
 */
public class RenderFrame extends Window {

	private static RenderFrame instance;

	/**
	 * Gets the current {@link RenderFrame} instance
	 *
	 * @return {@link RenderFrame}
	 */
	public static RenderFrame getInstance() {
		return instance;
	}

	private JFrame parent;
	private Frame currentFrame;
	private BufferedImage nativeImage;
	private int offsetX, offsetY;
	private int viewWidth, viewHeight;

	/**
	 * Create a new {@link RenderFrame} for the given window
	 *
	 * @param parent
	 *            {@link JFrame}
	 */
	public RenderFrame(JFrame parent) {
		instance = this;

		this.parent = parent;

		getRootPane().putClientProperty("Window.shadow", Boolean.FALSE);
		setAlwaysOnTop(true);
		// setUndecorated(true);
		setBackground(Color.black);
		setFocusableWindowState(false);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				parent.requestFocus();
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				if (e.getOppositeWindow() == parent)
					parent.requestFocus();
			}
		});
	}

	/**
	 * Starts the {@link RenderFrame}
	 */
	public void start() {
		// setVisible(true);
		// createBufferStrategy(2);
	}

	@Override
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

		if (nativeImage == null || parent.getWidth() != nativeImage.getWidth() || parent.getHeight() != nativeImage.getHeight())
			makeNewNativeImage(parent.getWidth(), parent.getHeight());

		frame.drawTo(nativeImage);

		Graphics g = st.getDrawGraphics();

		if (nativeImage != null)
			g.drawImage(nativeImage, 0, 0, parent.getWidth(), parent.getHeight(), null);
		else {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, parent.getWidth(), parent.getHeight());
		}

		g.dispose();
		st.show();

		try {
			st.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		dispose();
	}

	/**
	 * Determine if the {@link RenderFrame} should be shown
	 */
	public void updateIsVisible() {
		boolean visible = parent.isActive() && currentFrame != null;
		if (isVisible() != visible)
			setVisible(visible);
	}

	/**
	 * Update the size of the {@link RenderFrame}
	 */
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

	/**
	 * Sets the X offset of this {@link RenderFrame}
	 *
	 * @param offsetX
	 *            int
	 */
	public void setX(int offsetX) {
		this.offsetX = offsetX;
	}

	/**
	 * Sets the Y offset of this {@link RenderFrame}
	 *
	 * @param offsetY
	 *            int
	 */
	public void setY(int offsetY) {
		this.offsetY = offsetY;
	}

	/**
	 * Set the width of this {@link RenderFrame}
	 *
	 * @param width
	 *            int
	 */
	public void setWidth(int width) {
		viewWidth = width;
	}

	/**
	 * Set the height of this {@link RenderFrame}
	 *
	 * @param height
	 *            int
	 */
	public void setHeight(int height) {
		viewHeight = height;
	}

	private static final long serialVersionUID = -6876566152495162962L;

}
