package me.skykistler.smudgr.view;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileNotFoundException;

import me.skykistler.smudgr.Smudge;
import processing.core.PApplet;
import processing.core.PImage;

public class View extends PApplet {
	private Smudge op;
	private int displayWidth;
	private int displayHeight;

	public void init() {
		runSketch(new String[] { "--present" });
	}

	public void setSmudge(Smudge smudge) {
		op = smudge;
	}

	public void settings() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		displayWidth = (int) screenSize.getWidth();
		displayHeight = (int) screenSize.getHeight();

		size(displayWidth, displayHeight);
	}

	public void setup() {
		frameRate(30);

		if (op != null)
			try {
				op.init();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
	}

	public void draw() {
		if (op != null) {
			PImage img = op.render();

			fitToScreen(img);
			centerDrawImage(img);
		}
	}

	// TODO write our own resize method for different interpolations
	private void fitToScreen(PImage img) {
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

			img.resize(w, h);
		}
	}

	private void centerDrawImage(PImage img) {
		int x = displayWidth / 2 - img.width / 2;
		int y = displayHeight / 2 - img.height / 2;
		image(img, x, y);
	}
}
