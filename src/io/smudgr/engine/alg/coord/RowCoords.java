package io.smudgr.engine.alg.coord;

import io.smudgr.engine.alg.bound.Bound;
import io.smudgr.util.Frame;

public class RowCoords extends CoordFunction {

	@Override
	public String getName() {
		return "Rows";
	}

	@Override
	public void generate(Bound b, Frame img) {
		int boundWidth = b.getTranslatedWidth(img.getWidth());
		int boundHeight = b.getTranslatedHeight(img.getHeight());
		int boundX = b.getTranslatedX(img.getWidth());
		int boundY = b.getTranslatedY(img.getHeight());

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
