package io.smudgr.engine.alg.coord;

import io.smudgr.engine.param.BooleanParameter;

/**
 * The {@link StraightCoords} coordinate function simply creates a coordinate
 * set
 * in a column direction.
 */
public class StraightCoords extends CoordFunction {

	private BooleanParameter vertical = new BooleanParameter("Vertical", this, false);

	@Override
	public String getTypeName() {
		return "Straight";
	}

	@Override
	protected void generateCoordinates(int imageWidth, int imageHeight, int boundX, int boundY, int boundWidth, int boundHeight) {
		boolean isVertical = vertical.getValue();

		int firstDirection = isVertical ? boundWidth : boundHeight;
		int secondDirection = isVertical ? boundHeight : boundWidth;

		for (int i = 0; i < firstDirection; i++) {
			nextSet();

			for (int j = 0; j < secondDirection; j++) {
				int x = boundX + (isVertical ? i : j);
				int y = boundY + (isVertical ? j : i);

				nextPoint(x, y);
			}
		}
	}

}
