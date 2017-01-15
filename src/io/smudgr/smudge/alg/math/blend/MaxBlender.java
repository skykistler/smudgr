package io.smudgr.smudge.alg.math.blend;

import io.smudgr.smudge.alg.math.ColorHelper;

public class MaxBlender implements Blender {

	public String getName() {
		return "Max Blender";
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

		int maxRed = Math.max(redA, redB);
		int maxGreen = Math.max(greenA, greenB);
		int maxBlue = Math.max(blueA, blueB);
		int newAlpha = alphaB; /*- Because going with the background frame's alpha is a kind of safe bet */

		return ColorHelper.color(newAlpha, maxRed, maxGreen, maxBlue);
	}

}
