package io.smudgr.alg;

import java.util.ArrayList;

import io.smudgr.Smudge;
import io.smudgr.alg.math.ColumnCoords;
import io.smudgr.alg.math.CoordFunction;
import io.smudgr.alg.math.LinearFunction;
import io.smudgr.alg.math.UnivariateFunction;
import io.smudgr.alg.param.DoubleParameter;
import io.smudgr.alg.param.IntegerParameter;
import io.smudgr.model.Frame;

public class PixelShift extends Algorithm {

	private DoubleParameter amount = new DoubleParameter(this, "Amount", 0, 0, 1, 0.005);
	private IntegerParameter intervals = new IntegerParameter(this, "Intervals", 5, 1, 255, 1);

	UnivariateFunction scale = new LinearFunction();

	Frame orig;
	Frame shifted;

	public PixelShift(Smudge s) {
		super(s);
		amount.setContinuous(true);
	}

	public void init() {
		super.init();

		if (getCoordFunction() == null)
			setCoordFunction(new ColumnCoords());
	}

	public void execute(Frame img) {
		orig = img.copy();
		shifted = img.copy();

		CoordFunction cf = getCoordFunction();
		double shift = amount.getValue();
		int ints = intervals.getValue();

		int intervalWidth = (int) Math.ceil(cf.getCoordSet().size() / (double) ints);
		for (int n = 0; n < ints; n++) {
			int interval = n * intervalWidth;

			for (int i = 0; i < intervalWidth; i++) {
				int index = interval + i;
				if (index < cf.getCoordSet().size()) {
					ArrayList<Integer> coords = cf.getCoordSet().get(index);
					shift(coords, n * shift);
				}
			}
		}

		img.pixels = shifted.pixels;
	}

	public void shift(ArrayList<Integer> coords, double amount) {
		for (int i = 0; i < coords.size(); i++) {
			int shift = (int) (i + amount * coords.size());
			shift %= coords.size();

			shifted.pixels[coords.get(i)] = orig.pixels[coords.get(shift)];
		}
	}

	public String getName() {
		return "Pixel Shift";
	}

}
