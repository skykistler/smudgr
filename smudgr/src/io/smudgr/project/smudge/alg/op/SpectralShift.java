package io.smudgr.project.smudge.alg.op;

import java.util.Arrays;

import io.smudgr.project.smudge.alg.PixelIndexList;
import io.smudgr.project.smudge.alg.math.ColorHelper;
import io.smudgr.project.smudge.alg.math.univariate.ChromaFunction;
import io.smudgr.project.smudge.alg.math.univariate.HueFunction;
import io.smudgr.project.smudge.alg.math.univariate.LogFunction;
import io.smudgr.project.smudge.alg.math.univariate.LumaFunction;
import io.smudgr.project.smudge.alg.math.univariate.SinFunction;
import io.smudgr.project.smudge.param.BooleanParameter;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.project.smudge.param.UnivariateParameter;
import io.smudgr.project.smudge.util.Frame;

public class SpectralShift extends Operation {

	public String getName() {
		return "Spectral Shift";
	}

	NumberParameter shift = new NumberParameter("Shift", this, 0, 0, 255, 1);
	NumberParameter colors = new NumberParameter("Colors", this, 3, 1, 256, 1);
	NumberParameter palette = new NumberParameter("Palette", this, 27, 1, 27, 1);
	BooleanParameter sort = new BooleanParameter("Sort", this, false);
	BooleanParameter reverse = new BooleanParameter("Reverse", this, false);

	private UnivariateParameter function = new UnivariateParameter("Function", this, new LumaFunction());

	int buckets = 0;
	int[] values = null;
	int[] counters = null;

	boolean wasSorted = false;

	public void init() {
		shift.setContinuous(true);
		palette.setContinuous(true);

		function.add(new ChromaFunction());
		function.add(new HueFunction());
		function.add(new SinFunction());
		function.add(new LogFunction());
	}

	public void execute(Frame img) {
		boolean negate = reverse.getValue();

		buckets = colors.getIntValue();
		shift.setMax(buckets - 1);

		int paletteId = palette.getIntValue();
		int p1 = paletteId % 3;
		int p2 = (paletteId / 3) % 3;
		int p3 = (paletteId / 9) % 3;

		boolean shouldSort = sort.getValue();
		if (values == null || values.length != buckets || shouldSort != wasSorted) {
			values = new int[buckets];
			counters = new int[buckets];

			for (PixelIndexList coords : getAlgorithm().getSelectedPixels())
				for (int index = 0; index < coords.size(); index++) {
					int coord = coords.get(index);
					int i = getBucket(img.pixels[coord]);

					values[i] += img.pixels[coord];
					counters[i] += 1;
				}

			if (shouldSort)
				Arrays.sort(values);

			wasSorted = shouldSort;
		}

		int shift_amount = shift.getIntValue() % buckets;

		for (PixelIndexList coords : getAlgorithm().getSelectedPixels())
			for (int index = 0; index < coords.size(); index++) {
				int coord = coords.get(index);
				int val = getBucket(img.pixels[coord]);
				int bucket_index = Math.abs((val + shift_amount * (negate ? -1 : 1)) % buckets);

				int pixel = values[bucket_index] / (counters[bucket_index] + 1);
				int red = ColorHelper.red(pixel);
				int blue = ColorHelper.blue(pixel);
				int green = ColorHelper.green(pixel);

				int r = p1 == 0 ? red : p1 == 1 ? green : blue;
				int g = p2 == 0 ? green : p2 == 1 ? blue : red;
				int b = p3 == 0 ? blue : p3 == 1 ? red : green;

				img.pixels[coord] = ColorHelper.color(255, r, g, b);
			}

	}

	public int getBucket(int value) {
		return (int) (function.getValue().calculate(value) * (buckets - 1));
	}

}
