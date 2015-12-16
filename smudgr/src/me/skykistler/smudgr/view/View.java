package me.skykistler.smudgr.view;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileNotFoundException;

import me.skykistler.smudgr.Smudge;
import processing.core.PApplet;
import processing.core.PImage;

public class View extends PApplet {
	private Smudge smudge;
	private int displayWidth;
	private int displayHeight;

	public void init() {
		runSketch(new String[] { "--present" });
	}

	public void setSmudge(Smudge smudge) {
		this.smudge = smudge;
	}

	public void settings() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		displayWidth = (int) screenSize.getWidth();
		displayHeight = (int) screenSize.getHeight();

		size(displayWidth, displayHeight);
	}

	public void setup() {
		frameRate(30);

		if (smudge != null)
			try {
				smudge.init(this);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
	}

	public void draw() {
		if (smudge != null) {
			PImage img = smudge.render();

			centerDrawImage(fitToScreen(img));
		}
	}

	private PImage fitToScreen(PImage img) {
		// Scale down
		if (img.height > displayHeight || img.width > displayWidth) {
			int w;
			int h;

			if (img.width > img.height) {
				w = displayWidth;
				h = (int) (img.height * ((double) displayWidth / img.width));
			} else {
				h = displayHeight;
				w = (int) (img.width * ((double) displayHeight / img.height));
			}

			img.resize(w, h);
		}

		// Scale up
		if (img.height < displayHeight && img.width < displayWidth) {
			int w;
			int h;

			if (img.height > img.width) {
				h = displayHeight;
				w = (int) (img.width * ((double) displayHeight / img.height));
			} else {
				w = displayWidth;
				h = (int) (img.height * ((double) displayWidth / img.width));
			}

			img = scaleUp(img, w, h);
		}

		return img;
	}

	private PImage scaleUp(PImage img, int width, int height) {
		if (width < img.width || height < img.height)
			return null;

		double width_factor = (double) img.width / width;
		double height_factor = (double) img.height / height;

		PImage scaled = new PImage(width, height);

		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) {
				int mx = (int) Math.floor(i * width_factor);
				int my = (int) Math.floor(j * height_factor);
				int index = mx + my * img.width;
				if (index >= img.pixels.length)
					continue;
				scaled.pixels[i + j * width] = img.pixels[index];
			}

		scaled.updatePixels();

		return scaled;
	}

	private void centerDrawImage(PImage img) {
		int x = displayWidth / 2 - img.width / 2;
		int y = displayHeight / 2 - img.height / 2;
		image(img, x, y);
	}
}
