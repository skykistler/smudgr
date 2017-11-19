package io.smudgr.extensions.image.alg.math.blend;

/**
 * Only Eric's mind can create something so abstruse as the
 * {@link RevolvingBlender}. It alternates between returning the first color or
 * the second color every time it's called.
 */
public class RevolvingBlender implements Blender {

	@Override
	public String getName() {
		return "Revolving Blender";
	}

	int currentState = 0;

	@Override
	public int blend(int colorA, int colorB) {
		if (currentState == 1) {
			currentState = 0;
			return colorA;
		} else {
			currentState = 1;
			return colorB;
		}
	}
}
