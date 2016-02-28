package io.smudgr.source.smudge.alg.coord;

import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.bound.Bound;

public class AllCoords extends CoordFunction {

	public String getName() {
		return "";
	}

	protected void generate(Bound b, Frame img) {
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
