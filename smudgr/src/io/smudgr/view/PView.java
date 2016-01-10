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

		controller.setView(this);
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

			img = img.fitToSize(displayWidth, displayHeight);

			PImage frame = createImage(img.getWidth(), img.getHeight(), ARGB);
			frame.pixels = img.pixels;
			frame.updatePixels();

			centerDrawImage(frame);
		}
	}

	private void centerDrawImage(PImage img) {
		int x = displayWidth / 2 - img.width / 2;
		int y = displayHeight / 2 - img.height / 2;
		image(img, x, y);
	}

}
