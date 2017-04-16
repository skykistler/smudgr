package io.smudgr.engine.alg.coord;

import io.smudgr.engine.param.BooleanParameter;

/**
 * The {@link StraightCoords} coordinate function simply creates a coordinate
 * set
 * in a column direction.
 */
public class StraightCoords extends CoordFunction {

	private BooleanParameter vertical = new BooleanParameter("Vertical", this, false);

	// memory reuse
	private boolean isVertical;
	private int firstDirection, secondDirection, i, j, x, y;

	@Override
	public String getTypeName() {
		return "Straight Lines";
	}

	@Override
	public String getTypeIdentifier() {
		return "straight-coords";
	}

	@Override
	protected void generateCoordinates(int imageWidth, int imageHeight, int boundX, int boundY, int boundWidth, int boundHeight) {
		isVertical = vertical.getValue();

		firstDirection = isVertical ? boundWidth : boundHeight;
		secondDirection = isVertical ? boundHeight : boundWidth;

		for (i = 0; i < firstDirection; i++) {
			nextSet();

			for (j = 0; j < secondDirection; j++) {
				x = boundX + (isVertical ? i : j);
				y = boundY + (isVertical ? j : i);

				nextPoint(x, y);
			}
		}
	}

}
