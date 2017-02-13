package io.smudgr.engine.alg.op;

import java.util.Arrays;

import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.alg.math.ColorHelper;
import io.smudgr.engine.alg.math.univariate.ChromaFunction;
import io.smudgr.engine.alg.math.univariate.HueFunction;
import io.smudgr.engine.alg.math.univariate.LogFunction;
import io.smudgr.engine.alg.math.univariate.LumaFunction;
import io.smudgr.engine.alg.math.univariate.SinFunction;
import io.smudgr.engine.alg.math.univariate.UnivariateFunction;
import io.smudgr.engine.param.BooleanParameter;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.engine.param.UnivariateParameter;
import io.smudgr.util.Frame;

/**
 * Spectral Shift buckets pixels using their value as calculated by a function,
 * and replaces each pixel in that bucket with a distinct color. The amount of
 * buckets/colors, the palette to replace with, and the function to calculate
 * pixel values with are all configurable.
 */
public class SpectralShift extends ParallelOperation {

	@Override
	public String getElementName() {
		return "Spectral Shift";
	}

	NumberParameter shift = new NumberParameter("Shift", this, 0, 0, 255, 1);
	NumberParameter colors = new NumberParameter("Colors", this, 3, 1, 256, 1);
	NumberParameter palette = new NumberParameter("Palette", this, 27, 1, 27, 1);
	BooleanParameter sort = new BooleanParameter("Sort", this, false);
	BooleanParameter reverse = new BooleanParameter("Reverse", this, false);

	private UnivariateParameter functionParam = new UnivariateParameter("Function", this, new LumaFunction());

	int buckets = 0;
	int[] values = null;
	int[] counters = null;

	private UnivariateFunction function;
	private boolean wasSorted, negate, shouldSort;
	private int paletteId, p1, p2, p3, shift_amount;

	@Override
	public void onInit() {
		shift.setContinuous(true);
		palette.setContinuous(true);

		functionParam.add(new ChromaFunction());
		functionParam.add(new HueFunction());
		functionParam.add(new SinFunction());
		functionParam.add(new LogFunction());
	}

	@Override
	public void preParallel(Frame img) {
		function = functionParam.getValue();
		negate = reverse.getValue();

		buckets = colors.getIntValue();
		shift.setMax(buckets - 1);

		paletteId = palette.getIntValue();
		p1 = paletteId % 3;
		p2 = (paletteId / 3) % 3;
		p3 = (paletteId / 9) % 3;

		shouldSort = sort.getValue();
		if (values == null || values.length != buckets || shouldSort != wasSorted) {
			values = new int[buckets];
			counters = new int[buckets];

			for (PixelIndexList coords : getSelectedPixels())
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

		shift_amount = shift.getIntValue() % buckets;
	}

	@Override
	public ParallelOperationTask getParallelTask() {
		return new SpectralShiftTask();
	}

	class SpectralShiftTask extends ParallelOperationTask {

		private int index, coord, val, bucket_index, pixel, red, blue, green, r, g, b;

		@Override
		public void executeParallel(Frame img, PixelIndexList coords) {
			for (index = 0; index < coords.size(); index++) {
				coord = coords.get(index);
				val = getBucket(img.pixels[coord]);
				bucket_index = Math.abs((val + shift_amount * (negate ? -1 : 1)) % buckets);

				pixel = values[bucket_index] / (counters[bucket_index] + 1);
				red = ColorHelper.red(pixel);
				blue = ColorHelper.blue(pixel);
				green = ColorHelper.green(pixel);

				r = p1 == 0 ? red : p1 == 1 ? green : blue;
				g = p2 == 0 ? green : p2 == 1 ? blue : red;
				b = p3 == 0 ? blue : p3 == 1 ? red : green;

				img.pixels[coord] = ColorHelper.color(255, r, g, b);
			}
		}
	}

	private int getBucket(int value) {
		return (int) (function.calculate(value) * (buckets - 1));
	}

}
