package io.smudgr.project.smudge.alg.op;

import java.util.Arrays;
import java.util.Comparator;

import io.smudgr.project.smudge.alg.PixelIndexList;
import io.smudgr.project.smudge.alg.math.ChromaFunction;
import io.smudgr.project.smudge.alg.math.HueFunction;
import io.smudgr.project.smudge.alg.math.LogFunction;
import io.smudgr.project.smudge.alg.math.LumaFunction;
import io.smudgr.project.smudge.param.BooleanParameter;
import io.smudgr.project.smudge.param.UnivariateParameter;
import io.smudgr.project.smudge.util.Frame;

public class PixelSort extends Operation {

	private BooleanParameter reverse = new BooleanParameter("Reverse", this, false);
	private UnivariateParameter function = new UnivariateParameter("Function", this, new LumaFunction());

	private Integer[] toSort = null;
	private Comparator<Integer> comparator = null;

	public String getName() {
		return "Pixel Sort";
	}

	public void init() {
		function.add(new ChromaFunction());
		function.add(new HueFunction());
		function.add(new LogFunction());
	}

	public void execute(Frame img) {
		comparator = new Comparator<Integer>() {
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
		};

		for (PixelIndexList coords : getAlgorithm().getSelectedPixels())
			sort(img, coords);
	}

	public void sort(Frame img, PixelIndexList coords) {
		int sortSize = coords.size();

		if (toSort == null || toSort.length < sortSize)
			toSort = new Integer[sortSize];

		for (int i = 0; i < sortSize; i++)
			toSort[i] = img.pixels[coords.get(i)];

		Arrays.sort(toSort, 0, sortSize, comparator);

		for (int i = 0; i < sortSize; i++)
			img.pixels[coords.get(i)] = toSort[i];
	}
}
