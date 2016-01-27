package io.smudgr.alg.math;

public class ColorHelper {

	/**
	 * Converts a set of RGB values into a single color integer
	 * 
	 * @param r
	 *            red component 0-255
	 * @param g
	 *            green component 0-255
	 * @param b
	 *            blue component 0-255
	 * @return color as single integer
	 */
	public static int color(int a, int r, int g, int b) {
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	/**
	 * Get the alpha of an integer color
	 * 
	 * @param color
	 *            as single integer
	 * @return alpha int value 0-255
	 */
	public static int alpha(int color) {
		return (color >> 24) & 0xff;
	}

	/**
	 * Get the red of an integer color
	 * 
	 * @param color
	 *            as single integer
	 * @return red int value 0-255
	 */
	public static int red(int color) {
		return (color >> 16) & 0xff;
	}

	/**
	 * Get the green of an integer color
	 * 
	 * @param color
	 *            as single integer
	 * @return green int value 0-255
	 */
	public static int green(int color) {
		return (color >> 8) & 0xff;
	}

	/**
	 * Get the blue of an integer color
	 * 
	 * @param color
	 *            as single integer
	 * @return blue int value 0-255
	 */
	public static int blue(int color) {
		return color & 0xff;
	}
}
