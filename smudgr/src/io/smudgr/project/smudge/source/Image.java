package io.smudgr.project.smudge.source;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.smudgr.project.smudge.util.Frame;

public class Image implements Source {

	private String filename;
	private Frame frame;

	private double resizeFactor;
	private Frame resizedFrame;

	public Image(String filename) {
		this.filename = filename;
	}

	public void init() {
		try {
			BufferedImage loaded = ImageIO.read(new File(filename));

			if (loaded == null) {
				System.out.println("Unable to load: " + filename);
				return;
			}

			frame = new Frame(loaded);
			resizedFrame = frame.copy();
			resizeFactor = 1;
		} catch (IOException e) {
			System.out.println("Error loading: " + filename);
			e.printStackTrace();
		}
	}

	public void update() {

	}

	public Frame getFrame(double resizeFactor) {
		if (this.resizeFactor != resizeFactor) {
			this.resizeFactor = resizeFactor;
			resizedFrame = frame.resize(resizeFactor);
		}

		return resizedFrame;
	}

	public void dispose() {
		frame.dispose();
		resizedFrame.dispose();
	}

	public String toString() {
		return filename;
	}

}
