package io.smudgr.project.smudge.alg.op;

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

	public String getName() {
		return "Pixel Sort";
	}

	private BooleanParameter reverse = new BooleanParameter("Reverse", this, false);
	private UnivariateParameter function = new UnivariateParameter("Function", this, new LumaFunction());

	private int[] toSort = null;
	private UnivariateFunction comparator = null;

	// Declared for memory reuse
	private int lt, gt, i, swap, sortSize, ret;
	private double o1l, o2l;

	public void init() {
		function.add(new ChromaFunction());
		function.add(new HueFunction());
		function.add(new LogFunction());

		toSort = new int[1024];
	}

	public void execute(Frame img) {
		comparator = function.getValue();

		for (PixelIndexList coords : getAlgorithm().getSelectedPixels())
			sortList(img, coords);
	}

	private void sortList(Frame img, PixelIndexList coords) {
		sortSize = coords.size();

		if (toSort.length < sortSize)
			toSort = new int[sortSize];

		for (i = 0; i < sortSize; i++)
			toSort[i] = img.pixels[coords.get(i)];

		sort(toSort, 0, sortSize - 1);

		for (i = 0; i < sortSize; i++)
			img.pixels[coords.get(i)] = toSort[i];
	}

	private void sort(int[] a, int lo, int hi) {
		if (hi <= lo)
			return;

		if (less(a[hi], a[lo]))
			exch(a, lo, hi);

		lt = lo + 1;
		gt = hi - 1;
		i = lo + 1;
		while (i <= gt) {
			if (less(a[i], a[lo]))
				exch(a, lt++, i++);
			else if (less(a[hi], a[i]))
				exch(a, i, gt--);
			else
				i++;
		}
		exch(a, lo, --lt);
		exch(a, hi, ++gt);

		sort(a, lo, lt - 1);
		if (less(a[lt], a[gt]))
			sort(a, lt + 1, gt - 1);
		sort(a, gt + 1, hi);

	}

	private boolean less(int v, int w) {
		return compare(v, w) < 0;
	}

	private int compare(int v, int w) {
		o1l = comparator.calculate(v);
		o2l = comparator.calculate(w);

		ret = 0;
		if (o1l < o2l)
			ret = 1;
		if (o1l > o2l)
			ret = -1;

		if (reverse.getValue())
			ret *= -1;

		return ret;
	}

	private void exch(int[] a, int i, int j) {
		swap = a[i];
		a[i] = a[j];
		a[j] = swap;
	}
}
