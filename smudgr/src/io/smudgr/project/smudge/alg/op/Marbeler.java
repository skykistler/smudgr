package io.smudgr.project.smudge.alg.op;

import java.util.Random;

import io.smudgr.project.smudge.alg.math.lerp.CubicInterpolator;
import io.smudgr.project.smudge.alg.math.lerp.Interpolator;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.project.util.Frame;

public class Marbeler extends Operation {

	public String getName() {
		return "Cubic Marbeler";
	}

	NumberParameter freq = new NumberParameter("Frequency", this, 5, 1, 128);
	NumberParameter iterations = new NumberParameter("Iterations", this, 4, 0, 32, 1);
	NumberParameter mod = new NumberParameter("Strength", this, .3, 0, 1);
	NumberParameter seed = new NumberParameter("Seed", this, 0);
	NumberParameter offsetXY = new NumberParameter("Offset - X/Y", this, .75, 0, 1);
	NumberParameter offsetX = new NumberParameter("Offset - X", this, 0, 0, 1);
	NumberParameter offsetY = new NumberParameter("Offset - Y", this, 0, 0, 1);

	Interpolator intplt = new CubicInterpolator();

	boolean horizontal = false;
	Random rand;

	public void init() {
		offsetXY.setContinuous(true);
		offsetX.setContinuous(true);
		offsetY.setContinuous(true);
	}

	public void execute(Frame img) {
		rand = new Random(seed.getIntValue());
		horizontal = false;

		int n = iterations.getIntValue();
		int x = freq.getIntValue();

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
		int k = horizontal ? img.getHeight() : img.getWidth() - 1;
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
