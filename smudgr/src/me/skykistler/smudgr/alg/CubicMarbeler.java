package me.skykistler.smudgr.alg;

import java.util.Random;

import me.skykistler.smudgr.view.View;
import processing.core.PImage;

public class CubicMarbeler implements Algorithm {

	int x = 6;
	int n = 4;
	double mod = .5;
	double offsetXY = .75;
	double offsetX = 0;
	double offsetY = 0;
	double loop = 100;
	boolean autoScroll = false;

	boolean horizontal = false;
	Random rand;

	@Override
	public void execute(View processor, PImage img) {
		rand = new Random(0);
		horizontal = false;

		if (autoScroll) {
			offsetX += 1 / loop;
			offsetX = offsetX >= 1 ? 0 : offsetX;
			offsetX = offsetX < 0 ? 1 : offsetX;
		}

		for (int i = 0; i < n; i++) {
			double[] points = new double[x];
			for (int j = 0; j < x; j++) {
				double p = rand.nextDouble();
				points[j] = (p + 1) / 3;
			}

			double[] offsets = new double[horizontal ? img.width : img.height];
			double r = (double) offsets.length / x;
			for (int j = 0; j < offsets.length; j++) {
				int p = (int) Math.floor(x * j / offsets.length);

				double percentage = (j - p * r) / r;

				int pre = p - 1;
				double v0 = points[(pre < 0 ? x - pre : pre) % x];
				double v1 = points[p];
				double v2 = points[(p + 1) % x];
				double v3 = points[(p + 2) % x];
				offsets[j] = Math.max(interpolate(v0, v1, v2, v3, percentage), 0);
			}

			for (int j = 0; j < offsets.length; j++) {
				pushPixels(img, j, offsets[j]);
			}

			horizontal = !horizontal;
		}
	}

	// v0 = the point before a
	// v1 = the point a
	// v2 = the point b
	// v3 = the point after b
	// x = the percentage of how close to interpolate to point a
	public double interpolate(double v0, double v1, double v2, double v3, double x) {
		double P = (v3 - v2) - (v0 - v1);
		double Q = (v0 - v1) - P;
		double R = v2 - v0;
		double S = v1;

		return P * Math.pow(x, 3) + Q * Math.pow(x, 2) + R * x + S;
	}

	public void pushPixels(PImage img, int j, double amount) {
		int k = horizontal ? img.height : img.width - 1;
		double o = horizontal ? offsetY : offsetX;

		int offset = (int) Math.abs(Math.floor(k * (this.offsetXY + o + amount * mod)));
		int[] row = new int[k];

		for (int l = 0; l < k; l++) {
			if (horizontal)
				row[l] = img.pixels[j + l * img.width];
			else
				row[l] = img.pixels[l + j * img.width];
		}

		for (int l = 0; l < k; l++) {
			try {
				int index = (l + offset) % k;
				if (horizontal)
					img.pixels[j + l * img.width] = row[index];
				else
					img.pixels[l + j * img.width] = row[index];
			} catch (Exception e) {
				System.out.println(offset);
			}
		}
	}

}
