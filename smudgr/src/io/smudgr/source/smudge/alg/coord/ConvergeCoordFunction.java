package io.smudgr.source.smudge.alg.coord;

import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.bound.Bound;
import io.smudgr.source.smudge.alg.param.NumberParameter;

public class ConvergeCoordFunction extends CoordFunction {

	private NumberParameter centerX;
	private NumberParameter centerY;

	public String getName() {
		return "Gravity";
	}

	public void init(Algorithm a) {
		centerX = new NumberParameter(a, "Gravity Center X", .5, 0, 1, 0.005);
		centerY = new NumberParameter(a, "Gravity Center Y", .5, 0, 1, 0.005);
	}

	protected void generate() {
		Bound b = getBound();
		Frame img = getImage();

		int boundX = b.getTranslatedX(img);
		int boundY = b.getTranslatedY(img);
		int boundWidth = b.getTranslatedWidth(img);
		int boundHeight = b.getTranslatedHeight(img);

		int middleX = (int) (centerX.getValue() * boundWidth);
		int middleY = (int) (centerY.getValue() * boundHeight);

		int x, y;
		x = boundX;
		for (y = 0; y < boundHeight; y++)
			bresenham(x, boundY + y, middleX, middleY);

		x = boundX + boundWidth - 1;
		for (y = 0; y < boundHeight; y++)
			bresenham(x, boundY + y, middleX, middleY);

		y = boundY;
		for (x = 0; x < boundWidth; x++)
			bresenham(boundX + x, y, middleX, middleY);

		y = boundY + boundHeight - 1;
		for (x = 0; x < boundWidth; x++)
			bresenham(boundX + x, y, middleX, middleY);
	}

	public void bresenham(int x, int y, int centerX, int centerY) {
		int w = centerX - x;
		int h = centerY - y;
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
			nextPoint(x, y);
			numerator += shortest;
			if (!(numerator < longest)) {
				numerator -= longest;
				x += dx1;
				y += dy1;
			} else {
				x += dx2;
				y += dy2;
			}
		}

		nextSet();
	}

}
