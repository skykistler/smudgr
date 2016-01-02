package io.smudgr.alg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import io.smudgr.alg.math.ColumnCoords;
import io.smudgr.alg.math.CoordFunction;
import io.smudgr.alg.math.LumaFunction;
import io.smudgr.alg.math.UnivariateFunction;
import io.smudgr.alg.param.BooleanParameter;
import io.smudgr.alg.param.DoubleParameter;
import io.smudgr.view.View;
import processing.core.PImage;

public class PixelSort extends Algorithm {

	UnivariateFunction thresholdFunction = new LumaFunction();
	UnivariateFunction sortFunction = new LumaFunction();
	CoordFunction coordFunction = new ColumnCoords();
	DoubleParameter thresh = new DoubleParameter(this, "Threshold", .5, -.01, 1, .01);
	BooleanParameter reverseSort = new BooleanParameter(this, "Reverse Sort Direction", false);

	public String getName() {
		return "Pixel Sort";
	}

	ArrayList<ArrayList<Integer>> coordSet;
	PImage img = null;

	public void init(View processor) {
		super.init(processor);

		coordFunction.setBound(getMask());
		coordSet = coordFunction.getCoordSet();
	}

	public void execute(PImage img) {
		this.img = img;

		for (int i = 0; i < 1; i++) {
			sort(null);
		}
	}

	public void sort(ArrayList<Integer> coords) {
		int start = 0;
		int end = 0;

		while (end < coords.size() - 1) {
			start = getFirstNotThresh(coords, start);

			if (start < 0)
				break;

			end = getNextThresh(coords, start);

			int sortLength = end - start;

			Integer[] toSort = new Integer[sortLength];

			for (int i = 0; i < sortLength; i++) {
				toSort[i] = img.pixels[coords.get(start + i)];
			}

			Arrays.sort(toSort, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					double o1l = sortFunction.calculate(o1);
					double o2l = sortFunction.calculate(o1);

					int ret = 0;
					if (o1l < o2l)
						ret = 1;
					if (o1l > o2l)
						ret = -1;

					if (reverseSort.getValue())
						ret *= -1;

					return ret;
				}
			});

			for (int i = 0; i < sortLength; i++) {
				img.pixels[coords.get(start + i)] = (int) toSort[i];
			}

			start = end + 1;
		}
	}

	int getFirstNotThresh(ArrayList<Integer> coords, int start) {
		int run = start;
		if (start < coords.size()) {
			while (thresholdFunction.calculate(img.pixels[coords.get(run)]) < thresh.getValue()) {
				run++;
				if (run >= coords.size())
					return -1;
			}
		}
		return run;
	}

	int getNextThresh(ArrayList<Integer> coords, int start) {
		int run = start;
		while (thresholdFunction.calculate(img.pixels[coords.get(run)]) > thresh.getValue()) {
			run++;
			if (run >= coords.size())
				return coords.size() - 1;
		}
		return run;
	}
}
