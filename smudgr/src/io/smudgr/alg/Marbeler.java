package io.smudgr.alg;

import java.util.Random;

import io.smudgr.Smudge;
import io.smudgr.alg.math.CubicInterpolator;
import io.smudgr.alg.math.Interpolator;
import io.smudgr.alg.param.BooleanParameter;
import io.smudgr.alg.param.DoubleParameter;
import io.smudgr.alg.param.IntegerParameter;
import io.smudgr.model.Frame;

public class Marbeler extends Algorithm {

	IntegerParameter freq = new IntegerParameter(this, "Frequency", 5, 1, 128);
	IntegerParameter iterations = new IntegerParameter(this, "Iterations", 4, 0, 32, 1);
	DoubleParameter mod = new DoubleParameter(this, "Strength", .3, 0, 1);
	IntegerParameter seed = new IntegerParameter(this, "Seed", 0);
	DoubleParameter offsetXY = new DoubleParameter(this, "Offset - X/Y", .75, 0, 1);
	DoubleParameter offsetX = new DoubleParameter(this, "Offset - X", 0, 0, 1);
	DoubleParameter offsetY = new DoubleParameter(this, "Offset - Y", 0, 0, 1);
	BooleanParameter autoScroll = new BooleanParameter(this, "Auto-Scroll", false);

	Interpolator intplt = new CubicInterpolator();

	boolean horizontal = false;
	Random rand;

	public Marbeler(Smudge s) {
		super(s);
	}

	public String getName() {
		return "Cubic Marbeler";
	}

	@Override
	public void execute(Frame img) {
		rand = new Random(seed.getValue());
		horizontal = false;

		if (autoScroll.getValue()) {
			offsetXY.increment();
		}

		int n = iterations.getValue();
		int x = freq.getValue();

		for (int i = 0; i < n; i++) {
			double[] points = new double[x];
			for (int j = 0; j < x; j++) {
				double p = rand.nextDouble();
				points[j] = (p + 1) / 3;
			}

			double[] offsets = new double[horizontal ? img.getWidth() : img.getHeight()];
			double r = (double) offsets.length / x;
			for (int j = 0; j < offsets.length; j++) {
				int p = (int) Math.floor(x * j / offsets.length);

				double percentage = (j - p * r) / r;

				int pre = p - 1;
				double v0 = points[(pre < 0 ? x - pre : pre) % x];
				double v1 = points[p];
				double v2 = points[(p + 1) % x];
				double v3 = points[(p + 2) % x];
				offsets[j] = Math.max(intplt.interpolate(v0, v1, v2, v3, percentage), 0);
			}

			for (int j = 0; j < offsets.length; j++) {
				pushPixels(img, j, offsets[j]);
			}

			horizontal = !horizontal;
		}
	}

	public void pushPixels(Frame img, int j, double amount) {
		int k = horizontal ? img.getWidth() : img.getWidth() - 1;
		double o = horizontal ? offsetY.getValue() : offsetX.getValue();

		int offset = (int) Math.abs(Math.floor(k * (this.offsetXY.getValue() + o + amount * mod.getValue())));
		int[] row = new int[k];

		for (int l = 0; l < k; l++) {
			if (horizontal)
				row[l] = img.pixels[j + l * img.getWidth()];
			else
				row[l] = img.pixels[l + j * img.getWidth()];
		}

		for (int l = 0; l < k; l++) {
			try {
				int index = (l + offset) % k;
				if (horizontal)
					img.pixels[j + l * img.getWidth()] = row[index];
				else
					img.pixels[l + j * img.getWidth()] = row[index];
			} catch (Exception e) {
				System.out.println(offset);
			}
		}
	}

}
