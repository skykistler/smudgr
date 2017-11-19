package io.smudgr.extensions.image.alg.math.blend;

/**
 * The {@link Blender} interface provides a method for returning a blended color
 * value given two colors.
 */
public interface Blender {

	/**
	 * Gets the unique identifier of this {@link Blender}
	 *
	 * @return name {@link String}
	 */
	public String getName();

	/**
	 * Gets the blended value of two colors using this {@link Blender}
	 * function.
	 *
	 * @param colorA
	 *            {@code int} 1st color
	 * @param colorB
	 *            {@code int} 2nd color
	 * @return {@code int} blended color
	 */
	public int blend(int colorA, int colorB);

}
