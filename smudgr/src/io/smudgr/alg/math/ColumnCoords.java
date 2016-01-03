package io.smudgr.alg.math;

import io.smudgr.alg.bound.Bound;
import processing.core.PImage;

public class ColumnCoords extends CoordFunction {

	@Override
	public void generate() {
		Bound b = getBound();
		PImage img = getImage();

		int boundWidth = b.getTranslatedWidth(img);
		int boundHeight = b.getTranslatedHeight(img);
		int offsetX = b.getTranslatedX(img);
		int offsetY = b.getTranslatedY(img);

		for (int i = 0; i < boundWidth; i++) {
			nextSet();

			for (int j = 0; j < boundHeight; j++) {
				int x = offsetX + i;
				int y = offsetY + j;

				nextPoint(x, y);
			}
		}

	}

}
