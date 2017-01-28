package io.smudgr.engine.alg.math.blend;

/**
 * The {@link BitwiseOrBlender} returns the bitwise {@code |} (OR) result of
 * two colors.
 */
public class BitwiseOrBlender implements Blender {

	@Override
	public String getName() {
		return "OR";
	}

	@Override
	public int blend(int colorA, int colorB) {
		return colorA | colorB;
	}

}
