package io.smudgr.util.output;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.smudgr.app.controller.Controller;
import io.smudgr.util.Frame;

/**
 * The {@link ImageOutput} stream records a single PNG file for every frame
 * added.
 */
public class ImageOutput implements FrameOutput {
	private String path;
	private int width, height;

	/**
	 * Create a new {@link ImageOutput} with the given dimensions.
	 * <p>
	 * Frames added of a different dimension are fit to size and output at this
	 * dimension.
	 *
	 * @param name
	 *            {@link String}
	 * @param width
	 *            of output
	 * @param height
	 *            of output
	 */
	public ImageOutput(String name, int width, int height) {
		path = Controller.getInstance().getProject().getOutputPath() + name + "_" + System.currentTimeMillis() + ".png";
		this.width = width;
		this.height = height;
	}

	@Override
	public void open() {
	}

	@Override
	public void addFrame(Frame f) {
		System.out.println("Saving image to " + path);

		Frame toSave = f.resize(width, height);

		try {
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			toSave.drawTo(image);

			ImageIO.write(image, "png", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}

		toSave.dispose();
	}

	@Override
	public void close() {

	}

}
