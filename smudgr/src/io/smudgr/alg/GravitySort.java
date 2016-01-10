package io.smudgr.alg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import io.smudgr.Smudge;
import io.smudgr.alg.math.LumaFunction;
import io.smudgr.alg.math.UnivariateFunction;
import io.smudgr.alg.param.BooleanParameter;
import io.smudgr.alg.param.DoubleParameter;
import io.smudgr.model.Frame;

public class GravitySort extends Algorithm {

	public GravitySort(Smudge s) {
		super(s);
	}

	DoubleParameter centerXfrac = new DoubleParameter(this, "Center X", 0.5, 0, 1, 0.01);
	DoubleParameter centerYfrac = new DoubleParameter(this, "Center Y", 0.5, 0, 1, 0.01);

	BooleanParameter reverse = new BooleanParameter(this, "Reverse Sort", true);
	DoubleParameter threshold = new DoubleParameter(this, "Threshold", 127, -1, 256, 1);
	DoubleParameter rowStart = new DoubleParameter(this, "Starting Row Bound", 0.4, 0, 1);
	DoubleParameter rowEnd = new DoubleParameter(this, "Ending Row Bound", 0.6, 0, 1);
	DoubleParameter columnStart = new DoubleParameter(this, "Starting Column Bound", 0.4, 0, 1);
	DoubleParameter columnEnd = new DoubleParameter(this, "Ending Column Bound", 0.6, 0, 1);

	DoubleParameter degree = new DoubleParameter(this, "Degree of Rotation", 0, 0, 360, 1);

	UnivariateFunction thresholdFunc = new LumaFunction();
	UnivariateFunction sortFunc = new LumaFunction();

	int loops = 1;

	int row_start = 0;
	int row_end = 0;
	int column_start = 0;
	int column_end = 0;

	int centerY = 0;
	int centerX = 0;

	double sin = 0;
	double cos = 0;
	double tan = 0;
	ArrayList<Integer> coords;
	Frame img = null;

	public void execute(Frame img) {
		this.img = img;

		row_start = (int) (rowStart.getValue() * img.getHeight()) + 1;
		row_end = (int) (rowEnd.getValue() * img.getHeight()) - 1;

		column_start = (int) (columnStart.getValue() * img.getWidth()) + 1;
		column_end = (int) (columnEnd.getValue() * img.getWidth()) - 1;

		// centerY = (int) (row_start + (row_end - row_start) * centerYfrac.getValue() );
		// centerX = (int) (column_start + (column_end - column_start) * centerXfrac.getValue() );
		centerY = (int) (img.getHeight() * centerYfrac.getValue());
		centerX = (int) (img.getWidth() * centerXfrac.getValue());

		coords = new ArrayList<Integer>();

		int x, y;
		x = column_start;
		for (y = row_start; y < row_end; y++) {
			coords = bresenham(x, y);
			sort();
		}

		x = column_end - 1;
		for (y = row_start; y < row_end; y++) {
			coords = bresenham(x, y);
			sort();
		}

		y = row_start;
		for (x = column_start; x < column_end; x++) {
			coords = bresenham(x, y);
			sort();
		}

		y = row_end - 1;
		for (x = column_start; x < column_end; x++) {
			coords = bresenham(x, y);
			sort();
		}

	}

	// This function will return a list of coordinates
	public ArrayList<Integer> bresenham(int x, int y) {
		ArrayList<Integer> coords = new ArrayList<Integer>();

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
			coords.add(x + y * img.getWidth());
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

		return coords;

	}

	public void sort() {
		int start = 0;
		int end = 0;

		while (end < coords.size() - 1) {
			start = getFirstNotThresh(start);

			if (start < 0)
				break;

			end = getNextThresh(start);

			int sortLength = end - start;

			Integer[] toSort = new Integer[sortLength];

			for (int i = 0; i < sortLength; i++) {
				toSort[i] = img.pixels[coords.get(start + i)];
			}

			Arrays.sort(toSort, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					double o1l = sortFunc.calculate(o1);
					double o2l = sortFunc.calculate(o2);
					if (reverse.getValue()) {
						if (o1l > o2l)
							return 1;
						if (o1l < o2l)
							return -1;
					} else {
						if (o1l < o2l)
							return 1;
						if (o1l > o2l)
							return -1;
					}
					return 0;
				}
			});

			for (int i = 0; i < sortLength; i++) {
				img.pixels[coords.get(start + i)] = (int) toSort[i];
			}

			start = end + 1;
		}
	}

	int getFirstNotThresh(int start) {
		int run = start;
		if (start < coords.size()) {
			while (thresholdFunc.calculate(img.pixels[coords.get(run)]) < threshold.getValue()) {
				run++;
				if (run >= coords.size())
					return -1;
			}
		}
		return run;
	}

	int getNextThresh(int start) {
		int run = start;
		while (thresholdFunc.calculate(img.pixels[coords.get(run)]) > threshold.getValue()) {
			run++;
			if (run >= coords.size())
				return coords.size() - 1;
		}
		return run;
	}

	int rotateX(int x, int y) {
		int dx = (int) (x - centerX);
		int dy = (int) (y - centerY);
		double firstX = Math.round(dx - dy * tan + centerX);
		double firstY = Math.round(dy + centerY);
		dx = (int) (firstX - centerX);
		dy = (int) (firstY - centerY);
		double secondX = Math.round(dx + centerX);
		double secondY = Math.round(sin * dx + dy + centerY);
		dx = (int) (secondX - centerX);
		dy = (int) (secondY - centerY);
		int finalX = (int) (Math.round(dx - dy * tan + centerX));
		// int finalY = (int)(Math.round(dy + centerY));

		return finalX;
	}

	int rotateY(int x, int y) {
		int dx = (int) (x - centerX);
		int dy = (int) (y - centerY);
		double firstX = Math.round(dx - dy * tan + centerX);
		double firstY = Math.round(dy + centerY);
		dx = (int) (firstX - centerX);
		dy = (int) (firstY - centerY);
		double secondX = Math.round(dx + centerX);
		double secondY = Math.round(sin * dx + dy + centerY);
		dx = (int) (secondX - centerX);
		dy = (int) (secondY - centerY);
		// int finalX = (int)(Math.round(dx - dy*tan + centerX));
		int finalY = (int) (Math.round(dy + centerY));
		return finalY;
	}

	public String getName() {
		return "Gravity Sort";
	}

}