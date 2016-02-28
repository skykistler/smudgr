package io.smudgr.source.smudge.alg.op;

import java.util.Arrays;
import java.util.Comparator;

import gnu.trove.list.array.TIntArrayList;
import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.math.LumaFunction;
import io.smudgr.source.smudge.alg.math.UnivariateFunction;
import io.smudgr.source.smudge.param.BooleanParameter;

public class PixelSort extends Operation {

	BooleanParameter reverse = new BooleanParameter("Reverse", this, false);

	UnivariateFunction sortFunction = new LumaFunction();

	public String getName() {
		return "Pixel Sort";
	}

	public void execute(Frame img) {
		for (TIntArrayList coords : getAlgorithm().getSelectedPixels())
			sort(coords);
	}

	public void sort(TIntArrayList coords) {
		Integer[] toSort = new Integer[coords.size()];

		for (int i = 0; i < toSort.length; i++) {
			toSort[i] = img.pixels[coords.get(i)];
		}

		Arrays.sort(toSort, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				double o1l = sortFunction.calculate(o1);
				double o2l = sortFunction.calculate(o2);

				int ret = 0;
				if (o1l < o2l)
					ret = 1;
				if (o1l > o2l)
					ret = -1;

				if (reverse.getValue())
					ret *= -1;

				return ret;
			}
		});

		for (int i = 0; i < toSort.length; i++) {
			img.pixels[coords.get(i)] = (int) toSort[i];
		}
	}
}
