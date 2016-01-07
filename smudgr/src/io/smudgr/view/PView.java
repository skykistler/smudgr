package io.smudgr.view;

import java.awt.Dimension;
import java.awt.Toolkit;

import io.smudgr.Smudge;
import io.smudgr.controller.Controller;
import io.smudgr.model.Frame;
import processing.core.PApplet;
import processing.core.PImage;

public class PView extends PApplet implements View {
	private Controller controller;
	private int displayWidth;
	private int displayHeight;

	public PView(Controller controller) {
		this.controller = controller;
	}

	public void init() {
		runSketch(new String[] { "--present" });
	}

	public void settings() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		displayWidth = (int) screenSize.getWidth();
		displayHeight = (int) screenSize.getHeight();

		size(displayWidth, displayHeight);
	}

	public void setup() {
		frameRate(30);
	}

	public void draw() {
		Smudge smudge = controller.getSmudge();
		if (smudge != null) {
			Frame img = smudge.render();

			PImage frame = new PImage(img.getWidth(), img.getHeight());
			for (int i = 0; i < frame.width; i++)
				for (int j = 0; j < frame.height; j++) {
					int index = i + j * frame.width;
					frame.pixels[index] = img.pixels[index];
				}

			centerDrawImage(fitToScreen(frame));
		}
	}

	private PImage fitToScreen(PImage img) {
		// Scale down if needed
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
		} else
			// Scale up if needed
			if (img.height < displayHeight && img.width < displayWidth) {
			int w = (int) (img.width * ((double) displayHeight / img.height));
			int h = displayHeight;

			// img = scaleUp(img, w, h);
		}

		return img;
	}

	private void centerDrawImage(PImage img) {
		int x = displayWidth / 2 - img.width / 2;
		int y = displayHeight / 2 - img.height / 2;
		image(img, x, y);
	}

}
