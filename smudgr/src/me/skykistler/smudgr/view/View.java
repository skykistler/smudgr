package me.skykistler.smudgr.view;

import java.awt.Dimension;
import java.awt.Toolkit;

import me.skykistler.smudgr.Smudge;
import processing.core.PApplet;
import processing.core.PImage;

public class View extends PApplet {
	private Smudge op;
	private int displayWidth;
	private int displayHeight;

	public View(Smudge smudge) {
		op = smudge;
	}

	public void init() {
		PApplet.main(new String[] { "--present", getClass().getName() });
	}

	public void settings() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		displayWidth = (int) screenSize.getWidth();
		displayHeight = (int) screenSize.getHeight();

		size(displayWidth, displayHeight);
	}

	public void draw() {
		PImage img = op.render();

		fitToScreen(img);
		centerDrawImage(img);
	}

	// TODO write our own resize method for different interpolations
	private void fitToScreen(PImage img) {
		if (img.height < displayHeight && img.width < displayWidth) {
			if (img.height > img.width) {
				int h = displayHeight;
				int w = (int) (img.width * ((double) displayHeight / img.height));
				img.resize(w, h);
			} else {
				int w = displayWidth;
				int h = (int) (img.height * ((double) displayWidth / img.width));
				img.resize(w, h);
			}
		}
	}

	private void centerDrawImage(PImage img) {
		int x = displayWidth / 2 - img.width / 2;
		int y = displayHeight / 2 - img.height / 2;
		image(img, x, y);
	}
}
