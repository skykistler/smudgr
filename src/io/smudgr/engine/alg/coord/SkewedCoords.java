package io.smudgr.engine.alg.coord;

import gnu.trove.list.array.TIntArrayList;
import io.smudgr.engine.param.NumberParameter;

/**
 * The {@link SkewedCoords} coordinate function generates coordinates that are
 * parallel to each to each other at a constant parameterized angle.
 */
public class SkewedCoords extends CoordFunction {

	@Override
	public String getTypeName() {
		return "Diagonal Lines";
	}

	@Override
	public String getTypeIdentifier() {
		return "skewed-coords";
	}

	// Which end coordinate do we want
	NumberParameter endCoordOffset = new NumberParameter("Skew Degree", this, 0.6, 0, 2.0, 0.01);

	// The following selected coord components will be important later on in the
	// generate method
	private int selectedEndX;
	private int selectedEndY;

	@Override
	protected void generateCoordinates(int imageWidth, int imageHeight, int boundX, int boundY, int boundWidth, int boundHeight) {
		double offset = endCoordOffset.getValue();

		/*
		 * Set the possible end coordinates from the TWO start coords:
		 * (0, 0)
		 * (0, width-1)
		 */
		int possibleRangeSize = boundHeight + boundWidth + boundHeight - 2;
		int xs[] = new int[possibleRangeSize];
		int ys[] = new int[possibleRangeSize];

		fillPossibleEndCoords(boundX, boundY, boundWidth, boundHeight, xs, ys);
		selectEndPoint(boundWidth, boundHeight, offset, xs, ys);

		// Now that we have all possible end coords, we can move on to creating
		// coord sets through the coordinate basis

		createCoords(selectedEndX, selectedEndY, offset, boundWidth, boundHeight, boundX, boundY);

	}

	private void fillPossibleEndCoords(int offsetX, int offsetY, int width, int height, int xs[], int ys[]) {

		// count shouldn't exceed the length of xs and ys but why assert things
		// if I'm always right
		int index = 0;

		// We want to follow a very specific order for this to work
		for (int i = 0; i < height - 1; i++) {
			xs[index] = width - 1;
			ys[index] = i;
			index++;
		}
		for (int i = 0; i < width; i++) {
			xs[index] = (width - i - 1);
			ys[index] = height - 1;
			index++;
		}
		for (int i = 0; i < height - 1; i++) {
			xs[index] = 0;
			ys[index] = (height - i - 1);
			index++;
		}

		// at this point, we have all possible end coordinates separated by x
		// and y
		// I'm also not sure why I separated x and y into separate arrays, but
		// it looks better
	}

	// returns true if we should use start point 1, and false if start point 2
	private void selectEndPoint(int w, int h, double offset, int xs[], int ys[]) {

		// compute our end x and y for the coord basis this should probably be
		// its own thing
		boolean startPoint2 = false;
		if (offset > 1)
			startPoint2 = true;
		offset = offset % 1;

		int length = w + h - 3;

		int index = (int) (length * offset);

		if (startPoint2 == true)
			index += h;

		selectedEndX = xs[index];
		selectedEndY = ys[index];

	}

	private void createCoords(int endX, int endY, double offset, int width, int height, int xOffset, int yOffset) {

		TIntArrayList coordBasis = new TIntArrayList();

		// Now we just need to compute bresenham, based on what endX and endY
		// are...
		int startX = 0;
		int startY = 0;

		// if the offset parameter is > 1, then we use the second start point
		if (offset > 1)
			startX = width - 1;

		// Since start and end of the line are established, bresenham can be ran
		// results of the bresenham line approximation go into coordBasis
		bresenham(startX, startY, endX, endY, coordBasis);

		int basis[] = coordBasis.toArray(); // put the trove array list into an
											// int array for easier calculation

		int sweepXAmount = 0;
		int sweepYAmount = 0;

		// right here is where we reset the coords and start the sweeping
		// calculation for the coordsets
		if (endX == 0 || endX == width - 1) {
			incrementYs(-endY, basis);
			sweepYAmount = height + endY;
			sweepY(sweepYAmount, xOffset, yOffset, basis);
		}

		else {
			if (startX == width - 1) {
				incrementXs(-width, basis);
				sweepXAmount = (width - endX) + width;
			} else if (startX == 0) {
				incrementXs(-endX, basis);
				sweepXAmount = endX + width;
			}
			sweepX(sweepXAmount, xOffset, yOffset, basis);
		}

	}

	private void sweepX(int amount, int xOffset, int yOffset, int basis[]) {

		for (int i = 0; i < amount; i++) {

			for (int k = 0; k < basis.length / 2; k++) {

				int xi = 2 * k;
				int yi = 2 * k + 1;

				int x = basis[xi];
				int y = basis[yi];
				nextPoint(x + xOffset, y + yOffset);
			}
			nextSet();
			incrementXs(1, basis);
		}
	}

	private void sweepY(int amount, int xOffset, int yOffset, int basis[]) {
		for (int i = 0; i < amount; i++) {

			for (int k = 0; k < basis.length / 2; k++) {

				int xi = 2 * k;
				int yi = 2 * k + 1;

				int x = basis[xi];
				int y = basis[yi];
				nextPoint(x + xOffset, y + yOffset);
			}
			nextSet();
			incrementYs(1, basis);
		}
	}

	private void incrementYs(int amount, int basis[]) {

		for (int i = 1; i < basis.length; i += 2) {
			basis[i] += amount;
		}
	}

	private void incrementXs(int amount, int basis[]) {

		for (int i = 0; i < basis.length; i += 2) {
			basis[i] += amount;
		}
	}

	protected void bresenham(int x1, int y1, int x2, int y2, TIntArrayList coordBasis) {
		int w = x2 - x1;
		int h = y2 - y1;
		int dx1 = 0;
		int dy1 = 0;
		int dx2 = 0;
		int dy2 = 0;

		if (w < 0)
			dx1 = -1;
		else if (w > 0)
			dx1 = 1;
		if (h < 0)
			dy1 = -1;
		else if (h > 0)
			dy1 = 1;
		if (w < 0)
			dx2 = -1;
		else if (w > 0)
			dx2 = 1;

		int longest = Math.abs(w);
		int shortest = Math.abs(h);
		if (!(longest > shortest)) {
			longest = Math.abs(h);
			shortest = Math.abs(w);
			if (h < 0)
				dy2 = -1;
			else if (h > 0)
				dy2 = 1;
			dx2 = 0;
		}
		int numerator = longest >> 1;
		for (int i = 0; i <= longest; i++) {
			coordBasis.add(x1);
			coordBasis.add(y1);

			numerator += shortest;
			if (!(numerator < longest)) {
				numerator -= longest;
				x1 += dx1;
				y1 += dy1;
			} else {
				x1 += dx2;
				y1 += dy2;
			}
		}
	}

}
