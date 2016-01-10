package io.smudgr.alg;

import java.util.ArrayList;
import java.util.Arrays;

import io.smudgr.Smudge;
import io.smudgr.alg.math.ColumnCoords;
import io.smudgr.alg.math.CoordFunction;
import io.smudgr.alg.math.LumaFunction;
import io.smudgr.alg.math.UnivariateFunction;
import io.smudgr.alg.param.BooleanParameter;
import io.smudgr.alg.param.IntegerParameter;
import io.smudgr.model.Frame;

public class SpectralShift extends Algorithm {

	IntegerParameter shift = new IntegerParameter(this, "Shift", 0, 0, 255, 1);
	IntegerParameter colors = new IntegerParameter(this, "Colors", 3, 1, 256, 1);
	BooleanParameter sort = new BooleanParameter(this, "Sort", false);

	UnivariateFunction function = new LumaFunction();
	CoordFunction coordFunction = new ColumnCoords();

	Frame img = null;
	int buckets;

	public SpectralShift(Smudge s) {
		super(s);
	}

	public void init() {
		super.init();
		shift.setContinuous(true);

		coordFunction.setBound(getMask());
	}

	public void execute(Frame img) {
		if (this.img == null || (img.getWidth() != this.img.getWidth() || img.getHeight() != this.img.getHeight())) {
			coordFunction.setImage(img);
			coordFunction.update();
		}
		this.img = img;

		buckets = colors.getValue();

		int[] values = new int[buckets];
		int[] counters = new int[buckets];

		for (ArrayList<Integer> coords : coordFunction.getCoordSet())
			for (Integer coord : coords) {
				int i = getBucket(img.pixels[coord]);

				values[i] += img.pixels[coord];
				counters[i] += 1;
			}

		if (sort.getValue())
			Arrays.sort(values);

		int shift_amount = shift.getValue() % buckets;

		for (ArrayList<Integer> coords : coordFunction.getCoordSet())
			for (Integer coord : coords) {
				int val = getBucket(img.pixels[coord]);
				int index = (val + shift_amount) % buckets;

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
