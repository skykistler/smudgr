package io.smudgr.alg.math;

import io.smudgr.alg.bound.Bound;
import io.smudgr.model.Frame;

public class AllCoords extends CoordFunction {
	public String getName() {
		return "";
	}

	protected void generate() {
		Bound b = getBound();
		Frame img = getImage();

		int boundWidth = b.getTranslatedWidth(img);
		int boundHeight = b.getTranslatedHeight(img);
		int boundX = b.getTranslatedX(img);
		int boundY = b.getTranslatedY(img);

		for (int i = 0; i < boundWidth; i++) {
			for (int j = 0; j < boundHeight; j++) {
				int x = boundX + i;
				int y = boundY + j;

				nextPoint(x, y);
			}
		}

	}

}
