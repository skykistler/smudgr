package io.smudgr.app.output;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.smudgr.app.Controller;
import io.smudgr.util.Frame;

public class ImageOutput implements FrameOutput {
	private String path;
	private int width, height;

	public ImageOutput(String name, int width, int height) {
		path = Controller.getInstance().getProject().getOutputPath() + name + "_" + System.currentTimeMillis() + ".png";
		this.width = width;
		this.height = height;
	}

	public int getTargetFPS() {
		return 1;
	}

	public void open() {
	}

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

	public void close() {

	}

}
