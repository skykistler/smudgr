package io.smudgr.app.view;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import io.smudgr.project.smudge.util.Frame;

public interface View {
	public void start();

	public void update(Frame frame);

	public void stop();

	public default BufferedImage getNewNativeImage(int width, int height) {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = env.getDefaultScreenDevice();
		GraphicsConfiguration config = device.getDefaultConfiguration();

		return config.createCompatibleImage(width, height, Transparency.OPAQUE);
	}
}
