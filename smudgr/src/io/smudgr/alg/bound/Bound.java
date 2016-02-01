package io.smudgr.alg.bound;

import io.smudgr.source.Frame;

public class Bound {
	protected double offsetX;
	protected double offsetY;
	protected double width;
	protected double height;

	public Bound(double width, double height) {
		setWidth(width);
		setHeight(height);
	}

	public boolean containsPoint(Frame img, int x, int y) {
		int w = img.getWidth();
		int h = img.getHeight();

		boolean inImage = x >= 0 && y >= 0 && x < w && y < h;
		if (!inImage)
			return false;

		if (offsetX == 0 && offsetY == 0 && width == 1 && height == 1)
			return true;

		// Transform x and y coordinates to percentages
		double bX = (double) x / w;
		double bY = (double) y / h;

		if (bX < offsetX + width && x >= offsetX)
			if (bY < offsetY + height && y >= offsetY)
				return true;

		return false;
	}

	public double getOffsetX() {
		return offsetX;
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

	public void setHeight(double height) {
		if (height < 0)
			height = 0;
		if (height > 1)
			height = 1;
		this.height = height;
	}

	public int getTranslatedX(Frame image) {
		return (int) Math.floor(offsetX * image.getWidth());
	}

	public int getTranslatedY(Frame image) {
		return (int) Math.floor(offsetY * image.getHeight());
	}

	public int getTranslatedWidth(Frame image) {
		return (int) Math.floor(width * image.getWidth());
	}

	public int getTranslatedHeight(Frame image) {
		return (int) Math.floor(height * image.getHeight());
	}

}
