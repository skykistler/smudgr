package io.smudgr.alg.bound;

import processing.core.PImage;

public class Bound {
	private double offsetX;
	private double offsetY;
	private double width;
	private double height;

	private boolean fullBound;

	public Bound(double width, int height) {
		setWidth(width);
		setHeight(height);

		if (width == 1 && height == 1)
			fullBound = true;
	}

	public boolean containsPoint(PImage img, int x, int y) {
		// If fullbound, skip check
		if (fullBound)
			return true;

		// Transform x and y coordinates to percentages
		double bX = (double) x / img.width;
		double bY = (double) y / img.height;

		if (bX < width && x >= offsetX)
			if (bY < height && y >= offsetY)
				return true;

		return false;
	}

	public double getOffsetX() {
		return offsetX;
	}

	public int getTranslatedX(PImage image) {
		return (int) Math.floor(offsetX * image.width);
	}

	public void setOffsetX(double offsetX) {
		if (offsetX < 0)
			offsetX = 0;
		if (offsetX > 1)
			offsetX = 1;
		this.offsetX = offsetX;
	}

	public double getOffsetY() {
		return offsetY;
	}

	public int getTranslatedY(PImage image) {
		return (int) Math.floor(offsetY * image.height);
	}

	public void setOffsetY(double offsetY) {
		if (offsetY < 0)
			offsetY = 0;
		if (offsetY > 1)
			offsetY = 1;
		this.offsetY = offsetY;
	}

	public double getWidth() {
		return width;
	}

	public int getTranslatedWidth(PImage image) {
		return (int) Math.floor(width * image.width);
	}

	public void setWidth(double width) {
		if (width < 0)
			width = 0;
		if (width > 1)
			width = 1;
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public int getTranslatedHeight(PImage image) {
		return (int) Math.floor(height * image.height);
	}

	public void setHeight(double height) {
		if (height < 0)
			height = 0;
		if (height > 1)
			height = 1;
		this.height = height;
	}

}
