package io.smudgr.engine.alg.op;

import java.util.Random;

import io.smudgr.engine.alg.math.lerp.CubicInterpolator;
import io.smudgr.engine.alg.math.lerp.Interpolator;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.util.Frame;

/**
 * Marbeler iteratively shifts pixel columns in alternating directions for a
 * marble-esque distortion.
 */
public class Marbeler extends Operation {

	@Override
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
	Random rand = new Random();

	// Reusable memory :)
	double[] points = new double[0];
	double[] offsets, hOffsets, vOffsets;
	int[] row = new int[0];
	int n, x, i, j, point, pre, l, k, index, offset, offsetsLength, imgWidth, imgHeight;
	double p, r, percentage, v0, v1, v2, v3, o;
	double modParam, offsetXYParam, offsetYParam, offsetXParam;

	@Override
	public void init() {
		offsetXY.setContinuous(true);
		offsetX.setContinuous(true);
		offsetY.setContinuous(true);
	}

	@Override
	public void execute(Frame img) {
		imgWidth = img.getWidth();
		imgHeight = img.getHeight();

		rand.setSeed(seed.getIntValue());

		n = iterations.getIntValue();
		x = freq.getIntValue();
		modParam = mod.getValue();
		offsetXYParam = offsetXY.getValue();
		offsetYParam = offsetY.getValue();
		offsetXParam = offsetY.getValue();

		horizontal = false;

		// Only allocate new memory if the frequency param changes
		if (points.length != x) {
			points = new double[x];
		}

		for (i = 0; i < n; i++) {
			k = (horizontal ? imgHeight : imgWidth) - 1;
			o = horizontal ? offsetYParam : offsetXParam;
			offsetsLength = horizontal ? imgWidth : imgHeight;

			r = (double) offsetsLength / x;

			if (row.length != k)
				row = new int[k];

			if (offsets == null || offsets.length != offsetsLength) {
				if (horizontal) {
					if (vOffsets == null || vOffsets.length != offsetsLength)
						vOffsets = new double[offsetsLength];

					offsets = vOffsets;
				} else {
					if (hOffsets == null || hOffsets.length != offsetsLength)
						hOffsets = new double[offsetsLength];

					offsets = hOffsets;
				}
			}

			// Clear the points array
			for (j = 0; j < points.length; j++)
				points[j] = 0;

			for (j = 0; j < offsetsLength; j++)
				offsets[j] = 0;

			for (j = 0; j < x; j++) {
				p = rand.nextDouble();
				points[j] = (p + 1) / 3;
			}

			for (j = 0; j < offsetsLength; j++) {
				point = (int) Math.floor(x * j / offsetsLength);

				percentage = (j - point * r) / r;

				pre = point - 1;
				v0 = points[(pre < 0 ? x - pre : pre) % x];
				v1 = points[point];
				v2 = points[(point + 1) % x];
				v3 = points[(point + 2) % x];

				offsets[j] = Math.max(intplt.interpolate(v0, v1, v2, v3, percentage), 0);
			}

			for (j = 0; j < offsets.length; j++) {
				pushPixels(img, offsets[j]);
			}

			horizontal = !horizontal;
		}
	}

	private void pushPixels(Frame img, double amount) {
		offset = (int) Math.abs(Math.floor(k * (offsetXYParam + o + amount * modParam)));

		for (l = 0; l < k; l++) {
			if (horizontal)
				row[l] = img.pixels[j + l * imgWidth];
			else
				row[l] = img.pixels[l + j * imgWidth];
		}

		for (l = 0; l < k; l++) {
			try {
				index = (l + offset) % k;
				if (horizontal)
					img.pixels[j + l * imgWidth] = row[index];
				else
					img.pixels[l + j * imgWidth] = row[index];
			} catch (Exception e) {
				System.out.println(offset);
			}
		}
	}

}
