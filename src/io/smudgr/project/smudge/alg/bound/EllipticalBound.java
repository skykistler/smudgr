package io.smudgr.project.smudge.alg.bound;

import io.smudgr.util.Frame;

public class EllipticalBound extends Bound {

	public String getName() {
		return "Elliptical";
	}

	public boolean containsPoint(Frame img, int x, int y) {
		int w = img.getWidth();
		int h = img.getHeight();

		boolean inImage = x >= 0 && y >= 0 && x < w && y < h;
		if (!inImage)
			return false;

		// Create components to classic Ellipse equation with offsets:
		// (x^2 / offsetX^2) + (y^2 / offsetY^2) = 1

		double xOffset = offsetX.getValue() * w;
		double xRadius = (width.getValue() * w) / 2;
		double yOffset = offsetY.getValue() * h;
		double yRadius = (height.getValue() * h) / 2;

		int centerX = (int) ((x - (xOffset + xRadius)) * (x - (xOffset + xRadius)));
		int centerY = (int) ((y - (yOffset + yRadius)) * (y - (yOffset + yRadius)));
		double denomX = xRadius * xRadius;
		double denomY = yRadius * yRadius;

		double ellipse = (centerX / denomX) + (centerY / denomY);

		if (ellipse <= 1)
			return true;

		return false;
	}

}
