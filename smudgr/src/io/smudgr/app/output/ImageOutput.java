package io.smudgr.app.output;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.smudgr.app.Controller;
import io.smudgr.project.smudge.source.Frame;

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
			ImageIO.write(toSave.getBufferedImage(), "png", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {

	}

}
