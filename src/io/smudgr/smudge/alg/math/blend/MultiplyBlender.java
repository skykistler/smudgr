package io.smudgr.smudge.alg.math.blend;

import io.smudgr.smudge.alg.math.ColorHelper;

public class MultiplyBlender implements Blender {

	public String getName() {
		return "Multiply";
	}

	public int blend(int colorA, int colorB) {
		int redA = ColorHelper.red(colorA);
		int greenA = ColorHelper.green(colorA);
		int blueA = ColorHelper.blue(colorA);
		int alphaA = ColorHelper.alpha(colorA);

		int redB = ColorHelper.red(colorB);
		int greenB = ColorHelper.green(colorB);
		int blueB = ColorHelper.blue(colorB);
		int alphaB = ColorHelper.alpha(colorB);

		int compRed = multiply(redA, redB);
		int compGreen = multiply(greenA, greenB);
		int compBlue = multiply(blueA, blueB);
		int compAlpha = multiply(alphaA, alphaB);

		return ColorHelper.color(compAlpha, compRed, compGreen, compBlue);
	}

	private int multiply(int compA, int compB) {
		return Math.min(255, compA * compB);
	}

}
