package io.smudgr.project.smudge.alg.coord;

import io.smudgr.project.smudge.alg.bound.Bound;
import io.smudgr.project.smudge.source.Frame;

public class RowCoords extends CoordFunction {

	public String getName() {
		return "Rows";
	}

	public void generate(Bound b, Frame img) {
		int boundWidth = b.getTranslatedWidth(img);
		int boundHeight = b.getTranslatedHeight(img);
		int boundX = b.getTranslatedX(img);
		int boundY = b.getTranslatedY(img);

		for (int j = 0; j < boundHeight; j++) {
			nextSet();

			for (int i = 0; i < boundWidth; i++) {
				int x = boundX + i;
				int y = boundY + j;

				nextPoint(x, y);
			}
		}
	}

}
