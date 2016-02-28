package io.smudgr.source.smudge.alg.coord;

import gnu.trove.list.array.TIntArrayList;
import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.bound.Bound;
import io.smudgr.source.smudge.param.NumberParameter;

public class RadialCoordFunction extends CoordFunction {

	private NumberParameter innerRadius = new NumberParameter("Inner Radius", this, .5, 0, 1, 0.005);

	public String getName() {
		return "Radial";
	}

	protected void generate(Bound b, Frame img) {
		int boundX = b.getTranslatedX(img);
		int boundWidth = b.getTranslatedWidth(img);
		int boundY = b.getTranslatedY(img);
		int boundHeight = b.getTranslatedHeight(img);

		int radiusX = boundWidth / 2;
		int radiusY = boundHeight / 2;

		int innerR = (int) (radiusY >= radiusX ? radiusX - (int) radiusX * innerRadius.getValue() : radiusY - (int) radiusY * innerRadius.getValue());

		int _radiusY = radiusY;
		int _radiusX = radiusX;
		for (int i = 0; i < innerR; i++) {
			bresenham(_radiusX, _radiusY, boundX, boundY, img.getWidth(), img.getHeight());
			_radiusX--;
			_radiusY--;
		}
	}

	public void bresenham(int radiusX, int radiusY, int centerX, int centerY, int width, int height) {
		TIntArrayList c1 = new TIntArrayList();
		TIntArrayList c2 = new TIntArrayList();
		TIntArrayList c3 = new TIntArrayList();
		TIntArrayList c4 = new TIntArrayList();
		TIntArrayList c5 = new TIntArrayList();
		TIntArrayList c6 = new TIntArrayList();
		TIntArrayList c7 = new TIntArrayList();
		TIntArrayList c8 = new TIntArrayList();

		// first set of points
		int x, y;

		int twoASquare = 2 * radiusX * radiusX;
		int twoBSquare = 2 * radiusY * radiusY;
		int dx = radiusY * radiusY * (1 - 2 * radiusX);
		int dy = radiusX * radiusX;
		int error = 0;
		int stoppingX = twoBSquare * radiusX;
		int stoppingY = 0;

		x = radiusX;
		y = 0;
		while (stoppingX >= stoppingY) {
			c1.add((centerX + x) + ((centerY + y) * width));
			c4.insert(0, (centerX - x) + ((centerY + y) * width));
			c5.add((centerX - x) + ((centerY - y) * width));
			c8.insert(0, (centerX + x) + ((centerY - y) * width));

			y++;
			stoppingY += twoASquare;
			error += dy;
			dy += twoASquare;
			if ((2 * error + dx) > 0) {
				x--;
				stoppingX -= twoBSquare;
				error += dx;
				dx += twoBSquare;
			}
		}

		// second set of points
		dx = radiusY * radiusY;
		dy = radiusX * radiusX * (1 - 2 * radiusY);
		error = 0;
		stoppingX = 0;
		stoppingY = twoASquare * radiusY;
		x = 0;
		y = radiusY;
		while (stoppingX <= stoppingY) {
			c2.insert(0, (centerX + x) + ((centerY + y) * width));
			c3.add((centerX - x) + ((centerY + y) * width));
			c6.insert(0, (centerX - x) + ((centerY - y) * width));
			c7.add((centerX + x) + ((centerY - y) * width));

			x++;
			stoppingX += twoBSquare;
			error += dx;
			dx += twoBSquare;
			if ((2 * error + dy) > 0) {
				y--;
				stoppingY -= twoASquare;
				error += dy;
				dy += twoASquare;
			}
		}

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
