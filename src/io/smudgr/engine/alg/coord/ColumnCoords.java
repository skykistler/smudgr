package io.smudgr.engine.alg.coord;

import io.smudgr.engine.alg.bound.Bound;
import io.smudgr.util.Frame;

public class ColumnCoords extends CoordFunction {

	@Override
	public String getName() {
		return "Columns";
	}

	@Override
	protected void generate(Bound b, Frame img) {
		int boundWidth = b.getTranslatedWidth(img.getWidth());
		int boundHeight = b.getTranslatedHeight(img.getHeight());
		int boundX = b.getTranslatedX(img.getWidth());
		int boundY = b.getTranslatedY(img.getHeight());

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
