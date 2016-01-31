package io.smudgr.alg.coord;

import io.smudgr.alg.bound.Bound;
import io.smudgr.source.Frame;

public class ColumnCoords extends CoordFunction {

	public String getName() {
		return "Columns";
	}

	protected void generate() {
		Bound b = getBound();
		Frame img = getImage();

		int boundWidth = b.getTranslatedWidth(img);
		int boundHeight = b.getTranslatedHeight(img);
		int boundX = b.getTranslatedX(img);
		int boundY = b.getTranslatedY(img);

		for (int i = 0; i < boundWidth; i++) {
			nextSet();

			for (int j = 0; j < boundHeight; j++) {
				int x = boundX + i;
				int y = boundY + j;

				nextPoint(x, y);
			}
		}
	}

}
