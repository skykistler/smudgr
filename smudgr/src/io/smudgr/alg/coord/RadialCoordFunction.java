package io.smudgr.alg.coord;

import java.util.ArrayList;

import io.smudgr.alg.Algorithm;
import io.smudgr.alg.bound.Bound;
import io.smudgr.alg.param.NumberParameter;
import io.smudgr.model.Frame;

public class RadialCoordFunction extends CoordFunction {

	private NumberParameter innerRadius;

	public String getName() {
		return "Radial";
	}

	public void init(Algorithm a) {
		innerRadius = new NumberParameter(a, "Inner Radius", .5, 0, 1, 0.005);
	}

	protected void generate() {
		Bound b = getBound();
		Frame img = getImage();

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
		ArrayList<Integer> c1 = new ArrayList<Integer>();
		ArrayList<Integer> c2 = new ArrayList<Integer>();
		ArrayList<Integer> c3 = new ArrayList<Integer>();
		ArrayList<Integer> c4 = new ArrayList<Integer>();
		ArrayList<Integer> c5 = new ArrayList<Integer>();
		ArrayList<Integer> c6 = new ArrayList<Integer>();
		ArrayList<Integer> c7 = new ArrayList<Integer>();
		ArrayList<Integer> c8 = new ArrayList<Integer>();

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
			c4.add(0, (centerX - x) + ((centerY + y) * width));
			c5.add((centerX - x) + ((centerY - y) * width));
			c8.add(0, (centerX + x) + ((centerY - y) * width));

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
			c2.add(0, (centerX + x) + ((centerY + y) * width));
			c3.add((centerX - x) + ((centerY + y) * width));
			c6.add(0, (centerX - x) + ((centerY - y) * width));
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
