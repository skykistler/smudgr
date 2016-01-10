package io.smudgr.alg;

import java.util.ArrayList;
import java.util.Random;

import io.smudgr.Smudge;
import io.smudgr.alg.math.ColumnCoords;
import io.smudgr.alg.math.CoordFunction;
import io.smudgr.alg.math.LumaFunction;
import io.smudgr.alg.math.UnivariateFunction;
import io.smudgr.alg.param.IntegerParameter;
import io.smudgr.model.Frame;

public class SpectralShift extends Algorithm {

	IntegerParameter shift = new IntegerParameter(this, "Shift", 255, 0, 255, 1);
	IntegerParameter colors = new IntegerParameter(this, "Colors", 3, 1, 256, 1);

	UnivariateFunction function = new LumaFunction();
	CoordFunction coordFunction = new ColumnCoords();

	Frame img = null;

	int centerX, centerY;

	Random rand = new Random();
	public int[] values;
	public int[] counters;

	public SpectralShift(Smudge s) {
		super(s);
	}

	public void init() {
		super.init();
		coordFunction.setBound(getMask());
	}

	public void execute(Frame img) {
		if (this.img == null || (img.getWidth() != this.img.getWidth() || img.getHeight() != this.img.getHeight())) {
			coordFunction.setImage(img);
			coordFunction.update();
		}
		this.img = img;

		int buckets = colors.getValue();

		values = new int[buckets];
		counters = new int[buckets];

		for (ArrayList<Integer> coords : coordFunction.getCoordSet())
			for (Integer coord : coords) {
				int i = (int) function.calculate(img.pixels[coord]);

				values[i] += img.pixels[coord];
				counters[i] += 1;
			}

		for (int k = 0; k < buckets; k++) {
			if (counters[k] > 0)
				values[k] /= counters[k];
		}

		for (ArrayList<Integer> coords : coordFunction.getCoordSet())
			for (Integer coord : coords) {

				int val = (int) function.calculate(img.pixels[coord]);
				int index = (val + shift.getValue()) % buckets;

				int i = 0;
				while (values[index] == -1) {
					i++;
					index = (val + shift.getValue() + i) % buckets;
				}

				img.pixels[coord] = values[index];
			}

	}

	public String getName() {
		return "Spectral Shift";
	}

}
