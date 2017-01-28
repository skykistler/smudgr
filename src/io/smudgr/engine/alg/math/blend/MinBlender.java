package io.smudgr.engine.alg.math.blend;

import io.smudgr.engine.alg.math.ColorHelper;

/**
 * The {@link MinBlender} returns a color with the min red, min green, and min
 * blue between the two colors.
 */
public class MinBlender implements Blender {

	@Override
	public String getName() {
		return "Min Blender";
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

		int minRed = Math.min(redA, redB);
		int minGreen = Math.min(greenA, greenB);
		int minBlue = Math.min(blueA, blueB);
		int newAlpha = alphaB; /*- Because going with the background frame's alpha is a kind of safe bet */

		return ColorHelper.color(newAlpha, minRed, minGreen, minBlue);
	}

}
