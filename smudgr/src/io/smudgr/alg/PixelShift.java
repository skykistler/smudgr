package io.smudgr.alg;

import java.util.ArrayList;

import io.smudgr.Smudge;
import io.smudgr.alg.coord.ColumnCoords;
import io.smudgr.alg.coord.CoordFunction;
import io.smudgr.alg.math.LinearFunction;
import io.smudgr.alg.math.UnivariateFunction;
import io.smudgr.alg.param.NumberParameter;
import io.smudgr.model.Frame;

public class PixelShift extends Algorithm {

	private NumberParameter amount = new NumberParameter(this, "Amount", 0, 0, 1, 0.005);
	private NumberParameter intervals = new NumberParameter(this, "Intervals", 5, 1, 1000, 1);

	UnivariateFunction scale = new LinearFunction();

	Frame orig;
	Frame shifted;

	public PixelShift(Smudge s) {
		super(s);
		amount.setContinuous(true);
		setCoordFunction(new ColumnCoords());
	}

	public void init() {
		super.init();
	}

	public void execute(Frame img) {
		int size = getCoordFunction().getCoordSet().size();

		if (intervals.getMax() != size)
			intervals.setMax(size);

		orig = img.copy();
		shifted = img.copy();

		CoordFunction cf = getCoordFunction();
		double shift = amount.getValue();
		double ints = intervals.getValue();

		double intervalWidth = size / ints;
		for (int n = 0; n < ints; n++) {
			double interval = n * intervalWidth;

			for (int i = 0; i < intervalWidth; i++) {
				int index = (int) (interval + i);
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
