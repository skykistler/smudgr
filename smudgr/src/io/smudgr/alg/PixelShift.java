package io.smudgr.alg;

import io.smudgr.Smudge;
import io.smudgr.alg.math.LinearFunction;
import io.smudgr.alg.math.UnivariateFunction;
import io.smudgr.alg.param.DoubleParameter;
import io.smudgr.alg.param.IntegerParameter;
import io.smudgr.model.Frame;

public class PixelShift extends Algorithm {

	DoubleParameter amount = new DoubleParameter(this, "Amount", 0, 0, 1, 0.005);
	IntegerParameter intervals = new IntegerParameter(this, "Intervals", 5, 1, 255, 1);
	IntegerParameter direction = new IntegerParameter(this, "Direction", 1, -1, 1);

	UnivariateFunction scale = new LinearFunction();

	Frame orig;
	Frame shifted;

	public PixelShift(Smudge s) {
		super(s);
		amount.setContinuous(true);
	}

	public void execute(Frame img) {
		orig = img.copy();
		shifted = img.copy();
		int shift = (int) (amount.getValue() * img.getHeight());
		float ints = intervals.getValue();

		float intervalWidth = orig.getWidth() / ints;
		for (int n = 0; n < ints; n++) {
			float start = n * intervalWidth;

			for (int x = 0; x < intervalWidth; x++) {
				shift((int) (start + x), n * shift);
			}
		}

		img.pixels = shifted.pixels;
	}

	public void shift(int x, int amount) {
		if (x < orig.getWidth())
			for (int y = 0; y < orig.getHeight(); y++) {
				int shift = y + amount;
				shift %= orig.getHeight() - 1;

				shifted.set(x, y, orig.get(x, shift));
			}
	}

	public String getName() {
		return "Pixel Shift";
	}

}
