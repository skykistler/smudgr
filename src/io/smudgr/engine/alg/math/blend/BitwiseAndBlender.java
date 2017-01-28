package io.smudgr.engine.alg.math.blend;

/**
 * The {@link BitwiseAndBlender} returns the bitwise {@code &} (AND) result of
 * two colors.
 */
public class BitwiseAndBlender implements Blender {

	@Override
	public String getName() {
		return "AND";
	}

	@Override
	public int blend(int colorA, int colorB) {
		return colorA & colorB;
	}

}
