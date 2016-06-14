package io.smudgr.project.smudge.alg.math.blend;

import io.smudgr.project.smudge.alg.math.ColorHelper;

public class OverlayBlender implements Blender {

	public int blend(int colorA, int colorB) {
		int redA = ColorHelper.red(colorA);
		int greenA = ColorHelper.green(colorA);
		int blueA = ColorHelper.blue(colorA);
		int alphaA = ColorHelper.alpha(colorA);

		int redB = ColorHelper.red(colorB);
		int greenB = ColorHelper.green(colorB);
		int blueB = ColorHelper.blue(colorB);
		int alphaB = ColorHelper.alpha(colorB);

		int newRed = process(redA, redB);
		int newGreen = process(greenA, greenB);
		int newBlue = process(blueA, blueB);
		int newAlpha = process(alphaA, alphaB);

		return ColorHelper.color(newAlpha, newRed, newGreen, newBlue);

	}

	private int process(int a0, int b0) {
		double a = (double) a0;
		double b = (double) b0;

		int targetGreater = b > 0.5 ? 1 : 0;
		int targetLesser = b <= 0.5 ? 1 : 0;

		return (int) ((targetGreater * (1 - (1 - 2 * (b - 0.5)) * (1 - a)) + targetLesser * ((2 * b) * a)) * 255);

	}

	public String getName() {
		return "Overlay";
	}

}
