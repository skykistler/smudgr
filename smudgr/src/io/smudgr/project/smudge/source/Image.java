package io.smudgr.project.smudge.source;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.smudgr.project.smudge.util.Frame;

public class Image implements Source {

	private String filename;
	private Frame frame;

	private double lastDownsample = -1;
	private Frame downsampledFrame;

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
		} catch (IOException e) {
			System.out.println("Error loading: " + filename);
			e.printStackTrace();
		}
	}

	public void update() {

	}

	public Frame getFrame(double downsample) {
		if (lastDownsample != downsample) {
			lastDownsample = downsample;
			downsampledFrame = frame.downsample(downsample);
		}

		return downsampledFrame;
	}

	public void dispose() {
		frame = null;
	}

	public String toString() {
		return filename;
	}

}
