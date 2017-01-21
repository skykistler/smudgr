package io.smudgr.engine.alg.coord;

import java.math.BigInteger;

import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.param.NumberParameter;

/**
 * The {@link RadialCoords} coordinate function generates coordinates that
 * resemble a spiral.
 */
public class RadialCoords extends CoordFunction {

	@Override
	public String getName() {
		return "Spiral";
	}

	private NumberParameter innerRadius = new NumberParameter("Donut Hole", this, 0, 0, 1, 0.005);

	@Override
	protected void generate(int imageWidth, int imageHeight, int boundX, int boundY, int boundWidth, int boundHeight) {
		int radiusX = boundWidth / 2;
		int radiusY = boundHeight / 2;

		int innerR = (int) (radiusY >= radiusX ? radiusX - radiusX * innerRadius.getValue() : radiusY - radiusY * innerRadius.getValue());

		int _radiusY = radiusY;
		int _radiusX = radiusX;
		for (int i = 0; i < innerR; i++) {
			bresenham(_radiusX, _radiusY, boundX + boundWidth / 2, boundY + boundHeight / 2);
			_radiusX--;
			_radiusY--;
		}
	}

	protected void bresenham(int radiusX, int radiusY, int centerX, int centerY) {
		PixelIndexList c1 = new PixelIndexList();
		PixelIndexList c2 = new PixelIndexList();
		PixelIndexList c3 = new PixelIndexList();
		PixelIndexList c4 = new PixelIndexList();
		PixelIndexList c5 = new PixelIndexList();
		PixelIndexList c6 = new PixelIndexList();
		PixelIndexList c7 = new PixelIndexList();
		PixelIndexList c8 = new PixelIndexList();

		int x, y;

		// creating BigInteger variables to avoid a bunch of casting and
		// converting later on:
		BigInteger rX = BigInteger.valueOf(radiusX);
		BigInteger rY = BigInteger.valueOf(radiusY);
		BigInteger two = BigInteger.valueOf(2);
		BigInteger rXsquare = rX.multiply(rX);
		BigInteger rYsquare = rY.multiply(rY);
		BigInteger twoXsquare = rXsquare.multiply(two);
		BigInteger twoYsquare = rYsquare.multiply(two);
		BigInteger oneMinus2rX = BigInteger.valueOf(1).subtract(two.multiply(rX));
		BigInteger oneMinus2rY = BigInteger.valueOf(1).subtract(two.multiply(rY));

		// first while loop creates the first set of points in the ellipse

		BigInteger twoASquare = twoXsquare;
		BigInteger twoBSquare = twoYsquare;
		BigInteger dx = rYsquare.multiply(oneMinus2rX);
		BigInteger dy = rXsquare;
		BigInteger error = BigInteger.ZERO;
		BigInteger stoppingX = twoBSquare.multiply(rX);
		BigInteger stoppingY = BigInteger.ZERO;

		x = radiusX;
		y = 0;

		while (stoppingX.compareTo(stoppingY) >= 0) {

			// The symmetric points in each quadrant of the ellipse:
			int xplusx = centerX + x;
			int yplusy = centerY + y;
			int xminusx = centerX - x;
			int yminusy = centerY - y;

			int p1x = xplusx;
			int p1y = yplusy;
			if (boundContainsPoint(p1x, p1y))
				c1.add(p1x + p1y * imageWidth);

			int p2x = xminusx;
			int p2y = yplusy;
			if (boundContainsPoint(p2x, p2y))
				c4.insert(0, p2x + p2y * imageWidth);

			int p3x = xminusx;
			int p3y = yminusy;
			if (boundContainsPoint(p3x, p3y))
				c5.add(p3x + p3y * imageWidth);

			int p4x = xplusx;
			int p4y = yminusy;
			if (boundContainsPoint(p4x, p4y))
				c8.insert(0, p4x + p4y * imageWidth);

			y++;
			stoppingY = stoppingY.add(twoASquare); // stoppingY += twoASquare;
			error = error.add(dy); // error += dy;
			dy = dy.add(twoASquare); // dy += twoASquare;
			if ((error.multiply(BigInteger.valueOf(2)).add(dx)).compareTo(BigInteger.ZERO) > 0) {
				x--;
				stoppingX = stoppingX.subtract(twoBSquare); // stoppingX -=
															// twoBSquare;
				error = error.add(dx); // error += dx;
				dx = dx.add(twoBSquare); // dx += twoBSquare;
			}
		}

		// second set of points

		dx = rYsquare; // radiusY * radiusY;
		dy = rXsquare.multiply(oneMinus2rY); // radiusX1 * radiusX1 * (1 - 2 *
												// radiusY);
		error = BigInteger.ZERO;
		stoppingX = BigInteger.ZERO;
		stoppingY = twoASquare.multiply(rY); // twoASquare * radiusY;

		x = 0;
		y = radiusY;
		while (stoppingX.compareTo(stoppingY) <= 0) {

			// The symmetric points in each quadrant of the ellipse:
			int xplusx = centerX + x;
			int yplusy = centerY + y;
			int xminusx = centerX - x;
			int yminusy = centerY - y;

			int p1x = xplusx;
			int p1y = yplusy;
			if (boundContainsPoint(p1x, p1y))
				c2.insert(0, p1x + p1y * imageWidth);

			int p2x = xminusx;
			int p2y = yplusy;
			if (boundContainsPoint(p2x, p2y))
				c3.add(p2x + p2y * imageWidth);

			int p3x = xminusx;
			int p3y = yminusy;
			if (boundContainsPoint(p3x, p3y))
				c6.insert(0, p3x + p3y * imageWidth);

			int p4x = xplusx;
			int p4y = yminusy;
			if (boundContainsPoint(p4x, p4y))
				c7.add(p4x + p4y * imageWidth);

			x++;
			stoppingX = stoppingX.add(twoBSquare); // stoppingX += twoBSquare;
			error = error.add(dx); // error += dx;
			dx = dx.add(twoBSquare); // dx += twoBSquare;
			if ((error.multiply(BigInteger.valueOf(2)).add(dy)).compareTo(BigInteger.ZERO) > 0) {
				y--;
				stoppingY = stoppingY.subtract(twoASquare); // stoppingY -=
															// twoASquare;
				error = error.add(dy); // error += dy;
				dy = dy.add(twoASquare); // dy += twoASquare;
			}
		}

		/*
		 * The algorithm uses the symmetry of an ellipse to calculate points in
		 * each quadrant simultaneously
		 * through each loop. The following corrects the order of the calculated
		 * points to
		 * represent a single continuous ellipse curve.
		 */
		currentSet.addAll(c1);
		currentSet.addAll(c2);
		currentSet.addAll(c3);
		currentSet.addAll(c4);
		currentSet.addAll(c5);
		currentSet.addAll(c6);
		currentSet.addAll(c7);
		currentSet.addAll(c8);
		nextSet();

	}

}
