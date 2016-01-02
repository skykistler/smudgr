package io.smudgr.alg.math;

public class ColorHelper {

	/**
	 * Get the red of an integer color
	 * 
	 * @param color
	 * @return red value 0-255
	 */
	public static double red(int color) {
		return (color >> 16) & 0xff;
	}

	/**
	 * Get the green of an integer color
	 * 
	 * @param color
	 * @return green value 0-255
	 */
	public static double green(int color) {
		return (color >> 8) & 0xff;
	}

	/**
	 * Get the blue of an integer color
	 * 
	 * @param color
	 * @return blue value 0-255
	 */
	public static double blue(int color) {
		return color & 0xff;
	}
}
