package io.smudgr.project.smudge.alg.coord;

import io.smudgr.project.smudge.alg.bound.Bound;
import io.smudgr.util.Frame;

public class ColumnCoords extends CoordFunction {

	public String getName() {
		return "Columns";
	}

	protected void generate(Bound b, Frame img) {
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
