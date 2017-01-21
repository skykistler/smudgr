package io.smudgr.engine.alg.coord;

/**
 * The {@link RowCoords} coordinate function simply creates a coordinate set
 * in a column direction.
 */
public class RowCoords extends CoordFunction {

	@Override
	public String getName() {
		return "Rows";
	}

	@Override
	public void generate(int imageWidth, int imageHeight, int boundX, int boundY, int boundWidth, int boundHeight) {
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
