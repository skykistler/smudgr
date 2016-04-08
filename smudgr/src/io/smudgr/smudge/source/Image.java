package io.smudgr.smudge.source;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image implements Source {
	
	private String filename;
	private Frame frame;

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

	public Frame getFrame() {
		return frame;
	}

	public void dispose() {
		frame = null;
	}

	public String toString() {
		return filename;
	}

}
