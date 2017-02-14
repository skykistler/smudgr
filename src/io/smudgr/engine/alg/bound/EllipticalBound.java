package io.smudgr.engine.alg.bound;

import io.smudgr.engine.alg.Algorithm;

/**
 * The {@link EllipticalBound} implementation of {@link Bound} represents a
 * parameterized ellipse to bind an {@link Algorithm} to
 */
public class EllipticalBound extends Bound {

	@Override
	public String getTypeName() {
		return "Elliptical";
	}

	@Override
	public String getTypeIdentifier() {
		return "elliptical";
	}

	@Override
	public boolean containsPoint(int x, int y, int w, int h) {
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
