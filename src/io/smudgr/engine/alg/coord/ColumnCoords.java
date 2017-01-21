package io.smudgr.engine.alg.coord;

/**
 * The {@link ColumnCoords} coordinate function simply creates a coordinate set
 * in a column direction.
 */
public class ColumnCoords extends CoordFunction {

	@Override
	public String getName() {
		return "Columns";
	}

	@Override
	protected void generate(int imageWidth, int imageHeight, int boundX, int boundY, int boundWidth, int boundHeight) {
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
