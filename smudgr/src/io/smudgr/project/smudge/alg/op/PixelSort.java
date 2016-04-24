package io.smudgr.project.smudge.alg.op;

import java.util.Arrays;
import java.util.Comparator;

import io.smudgr.project.smudge.alg.PixelIndexList;
import io.smudgr.project.smudge.alg.math.ChromaFunction;
import io.smudgr.project.smudge.alg.math.HueFunction;
import io.smudgr.project.smudge.alg.math.LogFunction;
import io.smudgr.project.smudge.alg.math.LumaFunction;
import io.smudgr.project.smudge.alg.math.UnivariateFunction;
import io.smudgr.project.smudge.param.BooleanParameter;
import io.smudgr.project.smudge.param.UnivariateParameter;
import io.smudgr.project.smudge.util.Frame;

public class PixelSort extends Operation {

	BooleanParameter reverse = new BooleanParameter("Reverse", this, false);

	private UnivariateParameter function = new UnivariateParameter("Function", this, new LumaFunction());

	public String getName() {
		return "Pixel Sort";
	}

	public void init() {
		function.add(new ChromaFunction());
		function.add(new HueFunction());
		function.add(new LogFunction());
	}

	public void execute(Frame img) {
		for (PixelIndexList coords : getAlgorithm().getSelectedPixels())
			sort(img, coords);
	}

	public void sort(Frame img, PixelIndexList coords) {
		Integer[] toSort = new Integer[coords.size()];
		UnivariateFunction sortFunc = function.getValue();

		for (int i = 0; i < toSort.length; i++) {
			toSort[i] = img.pixels[coords.get(i)];
		}

		Arrays.sort(toSort, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				double o1l = sortFunc.calculate(o1);
				double o2l = sortFunc.calculate(o2);

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
