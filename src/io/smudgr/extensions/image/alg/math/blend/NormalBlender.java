package io.smudgr.extensions.image.alg.math.blend;

import io.smudgr.extensions.image.alg.math.ColorHelper;

/**
 * The {@link NormalBlender} returns a color blended using the alpha channel. If
 * the first color is fully opaque, none of the second color will be blended,
 * and vice versa.
 */
public class NormalBlender implements Blender {

	@Override
	public String getName() {
		return "Normal";
	}

	@Override
	public int blend(int colorA, int colorB) {
		int redA = ColorHelper.red(colorA);
		int greenA = ColorHelper.green(colorA);
		int blueA = ColorHelper.blue(colorA);
		int alphaA = ColorHelper.alpha(colorA);

		int redB = ColorHelper.red(colorB);
		int greenB = ColorHelper.green(colorB);
		int blueB = ColorHelper.blue(colorB);
		int alphaB = ColorHelper.alpha(colorB);

		double alphaCoef = alphaA / 255.0;
		double alphaBCoef = alphaB / 255.0;

		// Composite channels
		int compRed = createComposite(redA, redB, alphaCoef);
		int compGreen = createComposite(greenA, greenB, alphaCoef);
		int compBlue = createComposite(blueA, blueB, alphaCoef);
		int compAlpha = (int) (255 * (alphaCoef + alphaBCoef * (1 - alphaCoef)));

		return ColorHelper.color(compAlpha, compRed, compGreen, compBlue);
	}

	private int createComposite(int subcolorA, int subcolorB, double alphaCoef) {
		int compA = (int) (subcolorA * alphaCoef);
		int compB = (int) (subcolorB * (1 - alphaCoef));
		return compA + compB;
	}

}
