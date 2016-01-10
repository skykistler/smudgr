package io.smudgr.alg;

import io.smudgr.Smudge;
import io.smudgr.alg.math.LinearFunction;
import io.smudgr.alg.math.UnivariateFunction;
import io.smudgr.alg.param.DoubleParameter;
import io.smudgr.alg.param.IntegerParameter;
import io.smudgr.model.Frame;

public class PixelShift extends Algorithm {

	DoubleParameter amount = new DoubleParameter(this, "Amount", 0, 1, 0.01);
	IntegerParameter intervals = new IntegerParameter(this, "Intervals", 30, 1, 30, 1);
	IntegerParameter direction = new IntegerParameter(this, "Direction", 1, -1, 1);

	UnivariateFunction scale = new LinearFunction();

	Frame img = null;

	public PixelShift(Smudge s) {
		super(s);
	}

	public void execute(Frame img) {
		this.img = img;

		int intervalWidth = (int) Math.floor(img.getWidth() / intervals.getValue());
		for (int n = 0; n < intervals.getValue(); n++) {
			int start = n * intervalWidth;

			for (int x = 0; x < intervalWidth; x++) {
				shift(start + x, n * 50);
			}
		}
	}

	public void shift(int x, int amount) {
		for (int y = 0; y < img.getHeight(); y++) {
			int shift = y + amount;
			shift %= img.getHeight() - 1;

			img.set(x, y, img.get(x, shift));
		}
	}

	public String getName() {
		return "Pixel Shift";
	}

}
