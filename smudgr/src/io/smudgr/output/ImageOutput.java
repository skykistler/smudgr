package io.smudgr.output;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.smudgr.smudge.source.Frame;

public class ImageOutput implements FrameOutput {
	private String name;
	private int width, height;
	private int frameCount;

	public ImageOutput(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
	}

	public void open() {
		frameCount = 0;
	}

	public void addFrame(Frame f) {
		String output = name + "_" + frameCount + ".png";
		System.out.println("Saving image to " + output);

		Frame toSave = f.resize(width, height);

		try {
			ImageIO.write(toSave.getBufferedImage(), "png", new File(output));
			frameCount++;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {

	}

}
