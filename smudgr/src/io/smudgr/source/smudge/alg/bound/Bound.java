package io.smudgr.source.smudge.alg.bound;

import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.AlgorithmComponent;
import io.smudgr.source.smudge.param.NumberParameter;

public class Bound extends AlgorithmComponent {

	protected NumberParameter offsetX = new NumberParameter("Bound X", this, 0, 0, 1, 0.005);
	protected NumberParameter offsetY = new NumberParameter("Bound Y", this, 0, 0, 1, 0.005);
	protected NumberParameter width = new NumberParameter("Bound Width", this, 1, 0, 1, 0.005);
	protected NumberParameter height = new NumberParameter("Bound Height", this, 1, 0, 1, 0.005);

	public void init() {

	}

	public void update() {

	}

	public boolean containsPoint(Frame img, int x, int y) {
		int w = img.getWidth();
		int h = img.getHeight();

		double offsetX = this.offsetX.getValue();
		double offsetY = this.offsetY.getValue();
		double width = this.width.getValue();
		double height = this.height.getValue();

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
		return offsetX.getValue();
	}

	public void setOffsetX(double offsetX) {
		this.offsetX.setValue(offsetX);
	}

	public double getOffsetY() {
		return offsetY.getValue();
	}

	public void setOffsetY(double offsetY) {
		this.offsetY.setValue(offsetY);
	}

	public double getWidth() {
		return width.getValue();
	}

	public void setWidth(double width) {
		this.width.setValue(width);
	}

	public double getHeight() {
		return height.getValue();
	}

	public void setHeight(double height) {
		this.height.setValue(height);
	}

	public int getTranslatedX(Frame image) {
		return (int) Math.floor(offsetX.getValue() * image.getWidth());
	}

	public int getTranslatedY(Frame image) {
		return (int) Math.floor(offsetY.getValue() * image.getHeight());
	}

	public int getTranslatedWidth(Frame image) {
		return (int) Math.floor(width.getValue() * image.getWidth());
	}

	public int getTranslatedHeight(Frame image) {
		return (int) Math.floor(height.getValue() * image.getHeight());
	}

	public String getName() {
		return "Bound";
	}

}
