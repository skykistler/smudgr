package io.smudgr.alg.math;

import java.util.ArrayList;

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
			ArrayList<Integer> coords = new ArrayList<Integer>();

			for (int j = 0; j < boundHeight; j++) {
				int x = offsetX + i;
				int y = offsetY + j;

				if (getBound().containsPoint(img, x, y)) {
					int index = x + y * img.width;
					coords.add(index);
				}
			}

			coordSet.add(coords);
		}

	}

}
