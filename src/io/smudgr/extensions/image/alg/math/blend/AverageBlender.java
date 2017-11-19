package io.smudgr.extensions.image.alg.math.blend;

import io.smudgr.extensions.image.alg.math.ColorHelper;

/**
 * The {@link AverageBlender} finds the average of the red, blue, and green
 * color components separately and returns the resulting color.
 */
public class AverageBlender implements Blender {

	@Override
	public String getName() {
		return "Average";
	}

	@Override
	public int blend(int colorA, int colorB) {
		int redA = ColorHelper.red(colorA);
		int greenA = ColorHelper.green(colorA);
		int blueA = ColorHelper.blue(colorA);

		int redB = ColorHelper.red(colorB);
		int greenB = ColorHelper.green(colorB);
		int blueB = ColorHelper.blue(colorB);
		int alphaB = ColorHelper.alpha(colorB);

		int newRed = (redA + redB) / 2;
		int newGreen = (greenA + greenB) / 2;
		int newBlue = (blueA + blueB) / 2;

		return ColorHelper.color(alphaB, newRed, newGreen, newBlue);
	}

}
