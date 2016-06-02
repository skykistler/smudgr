package io.smudgr.project.smudge.alg.math.blend;

import io.smudgr.project.smudge.alg.math.ColorHelper;

public class MultiplyBlender implements Blender {

	public int blend(int colorA, int colorB) {
		int redA = ColorHelper.red(colorA);
		int greenA = ColorHelper.green(colorA);
		int blueA = ColorHelper.blue(colorA);
		int alphaA = ColorHelper.alpha(colorA);

		int redB = ColorHelper.red(colorB);
		int greenB = ColorHelper.green(colorB);
		int blueB = ColorHelper.blue(colorB);
		int alphaB = ColorHelper.alpha(colorB);
	}

	public String getName() {
		return "Multiply Blender";
	}

}
