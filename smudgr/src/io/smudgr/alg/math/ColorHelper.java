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
	public static int color(int r, int g, int b) {
		return (r << 16) | (g << 8) | b;
	}

	/**
	 * Get the red of an integer color
	 * 
	 * @param color
	 *            as single integer
	 * @return red double value 0-255
	 */
	public static double red(int color) {
		return (color >> 16) & 0xff;
	}

	/**
	 * Get the green of an integer color
	 * 
	 * @param color
	 *            as single integer
	 * @return green double value 0-255
	 */
	public static double green(int color) {
		return (color >> 8) & 0xff;
	}

	/**
	 * Get the blue of an integer color
	 * 
	 * @param color
	 *            as single integer
	 * @return blue double value 0-255
	 */
	public static double blue(int color) {
		return color & 0xff;
	}
}
