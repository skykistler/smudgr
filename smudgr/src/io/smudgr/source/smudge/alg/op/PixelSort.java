package io.smudgr.source.smudge.alg.op;

import java.util.Arrays;
import java.util.Comparator;

import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.ColorIndexList;
import io.smudgr.source.smudge.alg.math.ChromaFunction;
import io.smudgr.source.smudge.alg.math.HueFunction;
import io.smudgr.source.smudge.alg.math.LogFunction;
import io.smudgr.source.smudge.alg.math.LumaFunction;
import io.smudgr.source.smudge.param.BooleanParameter;
import io.smudgr.source.smudge.param.UnivariateParameter;

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
		for (ColorIndexList coords : getAlgorithm().getSelectedPixels())
			sort(coords);
	}

	public void sort(ColorIndexList coords) {
		Integer[] toSort = new Integer[coords.size()];

		for (int i = 0; i < toSort.length; i++) {
			toSort[i] = img.pixels[coords.get(i)];
		}

		Arrays.sort(toSort, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				double o1l = function.getValue().calculate(o1);
				double o2l = function.getValue().calculate(o2);

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
