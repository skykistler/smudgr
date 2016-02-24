package io.smudgr.out;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import io.smudgr.source.Frame;

public class Output {
	private String name;
	private int width, height;
	private ArrayList<Frame> frames = new ArrayList<Frame>();

	public Output(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
	}

	public void addFrame(Frame f) {
		frames.add(f);
	}

	public void finish() {
		if (frames.size() == 0)
			return;

		try {
			if (frames.size() == 1)
				saveImage();
			else
				saveGif();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveImage() throws IOException {
		String output = "output/" + name + ".png";
		System.out.println("Saving image to " + output);

		Frame toSave = frames.get(0).resize(width, height);

		ImageIO.write(toSave.getBufferedImage(), "png", new File(output));

	}

	private void saveGif() throws IOException {
		GifOutput writer = new GifOutput("output/" + name + ".gif", 30, true);

		writer.open();

		for (Frame f : frames)
			writer.write(f.getBufferedImage());

		writer.close();
	}
}
