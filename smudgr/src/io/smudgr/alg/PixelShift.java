package io.smudgr.alg;

import io.smudgr.alg.bound.Bound;
import io.smudgr.alg.math.LinearFunction;
import io.smudgr.alg.math.UnivariateFunction;
import io.smudgr.alg.param.DoubleParameter;
import io.smudgr.alg.param.IntegerParameter;
import io.smudgr.model.Frame;

public class PixelShift extends Algorithm {

	DoubleParameter amount = new DoubleParameter(this, "Shift Amount", 0, 1, 0.01);
	IntegerParameter intervals = new IntegerParameter(this, "Intervals", 5, 1, 30, 1);
	IntegerParameter direction = new IntegerParameter(this, "Direction", 1, -1, 1);

	UnivariateFunction scale = new LinearFunction();

	Frame img = null;

	public void execute(Frame img) {
		this.img = img;

		Bound b = getMask();

		int intervalWidth = (int) Math.floor(img.getWidth() / intervals.getValue());
		for (int n = 0; n < intervals.getValue(); n++) {
			int start = n * intervalWidth + (int) (b.getOffsetX() * img.getWidth());
			int end = start + (int) (b.getWidth() * img.getWidth());

			for (int x = start; x < end; x++) {
				shift(x, n);
			}
		}
	}

	public void shift(int x, int amount) {
		int boundY = getMask().getTranslatedY(img);
		int boundHeight = getMask().getTranslatedHeight(img);
		for (int y = boundY; y < boundHeight; y++) {
			int color = img.pixels[x + ((y + amount) % boundHeight) * img.getWidth()];

			img.pixels[x + y * img.getWidth()] = color;
		}
	}

	public String getName() {
		return "Pixel Shift";
	}

}
