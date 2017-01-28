package io.smudgr.engine.alg.math.blend;

import io.smudgr.engine.alg.math.ColorHelper;

/**
 * The {@link MaxBlender} returns a color with the max red, max green, and max
 * blue between the two colors.
 */
public class MaxBlender implements Blender {

	@Override
	public String getName() {
		return "Max Blender";
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

		int maxRed = Math.max(redA, redB);
		int maxGreen = Math.max(greenA, greenB);
		int maxBlue = Math.max(blueA, blueB);
		int newAlpha = alphaB; /*- Because going with the background frame's alpha is a kind of safe bet */

		return ColorHelper.color(newAlpha, maxRed, maxGreen, maxBlue);
	}

}
