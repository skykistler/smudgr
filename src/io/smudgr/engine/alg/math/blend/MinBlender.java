package io.smudgr.engine.alg.math.blend;

import io.smudgr.engine.alg.math.ColorHelper;

public class MinBlender implements Blender {

	public String getName() {
		return "Min Blender";
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

		int minRed = Math.min(redA, redB);
		int minGreen = Math.min(greenA, greenB);
		int minBlue = Math.min(blueA, blueB);
		int newAlpha = alphaB; /*- Because going with the background frame's alpha is a kind of safe bet */

		return ColorHelper.color(newAlpha, minRed, minGreen, minBlue);
	}

}
