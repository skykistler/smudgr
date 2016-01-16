package io.smudgr.alg;

import java.util.ArrayList;
import java.util.Arrays;

import io.smudgr.Smudge;
import io.smudgr.alg.math.LumaFunction;
import io.smudgr.alg.math.UnivariateFunction;
import io.smudgr.alg.param.BooleanParameter;
import io.smudgr.alg.param.NumberParameter;
import io.smudgr.model.Frame;

public class SpectralShift extends Algorithm {

	NumberParameter shift = new NumberParameter(this, "Shift", 0, 0, 255, 1);
	NumberParameter colors = new NumberParameter(this, "Colors", 3, 1, 256, 1);
	BooleanParameter sort = new BooleanParameter(this, "Sort", false);
	BooleanParameter reverse = new BooleanParameter(this, "Reverse", false);

	UnivariateFunction function = new LumaFunction();

	int buckets;

	public SpectralShift(Smudge s) {
		super(s);
		shift.setContinuous(true);
	}

	public void execute(Frame img) {
		buckets = colors.getIntValue();
		shift.setMax(buckets - 1);

		boolean negate = reverse.getValue();

		int[] values = new int[buckets];
		int[] counters = new int[buckets];

		for (ArrayList<Integer> coords : getCoordFunction().getCoordSet())
			for (Integer coord : coords) {
				int i = getBucket(img.pixels[coord]);

				values[i] += img.pixels[coord];
				counters[i] += 1;
			}

		if (sort.getValue())
			Arrays.sort(values);

		int shift_amount = shift.getIntValue() % buckets;

		for (ArrayList<Integer> coords : getCoordFunction().getCoordSet())
			for (Integer coord : coords) {
				int val = getBucket(img.pixels[coord]);
				int index = Math.abs((val + shift_amount * (negate ? -1 : 1)) % buckets);

				img.pixels[coord] = values[index] / (counters[index] + 1);
			}

	}

	public int getBucket(int value) {
		return (int) (function.calculate(value) * (buckets - 1));
	}

	public String getName() {
		return "Spectral Shift";
	}

}
