package io.smudgr.engine.alg.math;

/**
 * The {@link ColorHelper} utility provides small helper functions for common
 * color operations.
 */
public class ColorHelper {

	/**
	 * Converts a set of RGB values into a single color integer
	 *
	 * @param a
	 *            alpha component 0-255
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
	 * Converts given hue, saturation, and value into RGB color integer.
	 *
	 * @param h
	 *            hue 0-1
	 * @param s
	 *            saturation 0-1
	 * @param v
	 *            value 0-1
	 * @return RGB color integer
	 */
	public static int hsv(double h, double s, double v) {
		int r, g, b;
		if (s == 0) {
			r = (int) (v * 255);
			g = (int) (v * 255);
			b = (int) (v * 255);
		} else {
			double var_r, var_g, var_b;

			double var_h = h * 6;

			if (var_h == 6)
				var_h = 0;

			int var_i = (int) var_h;
			double var_1 = v * (1 - s);
			double var_2 = v * (1 - s * (var_h - var_i));
			double var_3 = v * (1 - s * (1 - (var_h - var_i)));

			if (var_i == 0) {
				var_r = v;
				var_g = var_3;
				var_b = var_1;
			} else if (var_i == 1) {
				var_r = var_2;
				var_g = v;
				var_b = var_1;
			} else if (var_i == 2) {
				var_r = var_1;
				var_g = v;
				var_b = var_3;
			} else if (var_i == 3) {
				var_r = var_1;
				var_g = var_2;
				var_b = v;
			} else if (var_i == 4) {
				var_r = var_3;
				var_g = var_1;
				var_b = v;
			} else {
				var_r = v;
				var_g = var_1;
				var_b = var_2;
			}

			r = (int) (var_r * 255);
			g = (int) (var_g * 255);
			b = (int) (var_b * 255);
		}

		return ColorHelper.color(255, r, g, b);

	}

	/**
	 * Gets the saturation of a given color
	 *
	 * @param color
	 *            RGB integer
	 * @return saturation 0-1
	 */
	public static double saturation(int color) {
		double red = red(color) / 255.0;
		double green = green(color) / 255.0;
		double blue = blue(color) / 255.0;

		double min = Math.min(red, Math.min(green, blue));
		double max = Math.max(red, Math.max(green, blue));
		double del_max = max - min;

		if (del_max == 0)
			return 0;

		return del_max / max;

	}

	/**
	 * Gets the hue of a given color
	 *
	 * @param color
	 *            RGB integer
	 * @return hue 0-1
	 */
	public static double hue(int color) {
		double red = red(color) / 255.0;
		double green = green(color) / 255.0;
		double blue = blue(color) / 255.0;

		double min = Math.min(red, Math.min(green, blue));
		double max = Math.max(red, Math.max(green, blue));

		double del_max = max - min;

		if (del_max == 0) {
			return 0.0;
		}

		double del_R = (((max - red) / 6) + (max / 2)) / max;
		double del_G = (((max - green) / 6) + (max / 2)) / max;
		double del_B = (((max - blue) / 6) + (max / 2)) / max;

		double hue = 0;

		if (red == max)
			hue = del_B - del_G;
		else if (green == max)
			hue = (1 / 3) + del_R - del_B;
		else if (blue == max)
			hue = (2 / 3) + del_G - del_R;

		if (hue < 0)
			hue += 1;
		if (hue > 1)
			hue -= 1;

		return hue;

	}

	/**
	 * Gets the chroma value of a given color
	 *
	 * @param color
	 *            RGB integer
	 * @return chroma value 0-1
	 */
	public static double chroma(int color) {

		// double red = red(color) / 255.0;
		// double green = green(color) / 255.0;
		// double blue = blue(color) / 255.0;
		// double alpha = (2 * red - green - blue) / 2;
		// double beta = (Math.sqrt(3.0) / 2.0) * (green - blue);
		// return Math.sqrt(alpha * alpha + beta * beta);

		int red = ColorHelper.red(color);
		int green = ColorHelper.green(color);
		int blue = ColorHelper.blue(color);
		int max = Math.max(red, Math.max(green, blue));
		int min = Math.min(red, Math.min(green, blue));

		return (max - min) / 255.0;

	}

	/**
	 * Gets RGB color integer given hue, luma, and chroma
	 *
	 * @param hue
	 *            0-255
	 * @param luma
	 *            0-1
	 * @param chroma
	 *            0-1
	 * @return RGB color integer
	 */
	public static int hlc(int hue, double luma, double chroma) {
		double h = (hue) / 60.0;
		double x = chroma * (1 - Math.abs(Math.IEEEremainder(h, 2) - 1));

		double r, g, b;
		if (h == -1) {// undefined hue
			r = 0;
			g = 0;
			b = 0;
		} else if (h >= 0 && h < 1) {
			r = chroma;
			g = x;
			b = 0;
		} else if (h >= 1 && h < 2) {
			r = x;
			g = chroma;
			b = 0;
		} else if (h >= 2 && h < 3) {
			r = 0;
			g = chroma;
			b = x;
		} else if (h >= 3 && h < 4) {
			r = 0;
			g = x;
			b = chroma;
		} else if (h >= 5 && h < 5) {
			r = x;
			g = 0;
			b = chroma;
		} else {
			r = chroma;
			g = 0;
			b = x;
		}

		double m = luma - (0.3 * r + 0.59 * g + 0.11 * b);
		int red = (int) (255 * (r + m));
		int green = (int) (255 * (g + m));
		int blue = (int) (255 * (b + m));

		return ColorHelper.color(255, red, green, blue);
	}

	/**
	 * Shift a given color by the given hue degrees, saturation, and value
	 *
	 * @param color
	 *            RGB integer
	 * @param degrees
	 *            of hue shift
	 * @param saturation
	 *            amount 0-1
	 * @param value
	 *            amount 0-1
	 * @return new RGB color integer
	 */
	public static int modifyHSV(int color, int degrees, double saturation, double value) {
		double r = red(color) / 255.0;
		double g = green(color) / 255.0;
		double b = blue(color) / 255.0;

		double h = 0, s, v;

		double rotation = degrees / 360.0;

		double min = Math.min(r, Math.min(g, b));
		double max = Math.max(r, Math.max(g, b));
		double deltaMax = max - min;

		v = max;

		if (deltaMax == 0) {
			h = 0;
			s = 0;
		} else {
			s = deltaMax / max;

			double delR, delG, delB;
			delR = (((max - r) / 6) + (deltaMax / 2)) / deltaMax;
			delG = (((max - g) / 6) + (deltaMax / 2)) / deltaMax;
			delB = (((max - b) / 6) + (deltaMax / 2)) / deltaMax;

			if (r == max)
				h = delB - delG;
			else if (g == max)
				h = 0.333 + delR - delB;
			else if (b == max)
				h = 0.666 + delG - delR;

			if (h < 0)
				h += 1;
			if (h > 1)
				h -= 1;

		}

		// This part affects HSV
		h += rotation;
		if (h < 0)
			h += 1;
		if (h > 1)
			h -= 1;

		s += saturation;
		if (s < 0)
			s += 1;
		if (s > 1)
			s -= 1;

		v += value;
		if (v < 0)
			v += 1;
		if (v > 1)
			v -= 1;

		// This last part converts back to RGB
		double newR, newG, newB;

		if (s == 0) {
			newR = v;
			newG = v;
			newB = v;
		} else {
			double varH, var1, var2, var3;
			int varI;

			varH = h * 6;
			if (varH == 6)
				varH = 0;
			varI = (int) varH;
			var1 = (v * (1 - s));
			var2 = (v * (1 - s * (varH - varI)));
			var3 = (v * (1 - s * (1 - (varH - varI))));

			switch (varI) {
				case 0:
					newR = v;
					newG = var3;
					newB = var1;
					break;
				case 1:
					newR = var2;
					newG = v;
					newB = var1;
					break;
				case 2:
					newR = var1;
					newG = v;
					newB = var3;
					break;
				case 3:
					newR = var1;
					newG = var2;
					newB = v;
					break;
				case 4:
					newR = var3;
					newG = var1;
					newB = v;
					break;
				default:
					newR = v;
					newG = var1;
					newB = var2;
			}
		}

		int red = (int) (newR * 255);
		int green = (int) (newG * 255);
		int blue = (int) (newB * 255);

		return color(255, red, green, blue);
	}

	/**
	 * Converts a set of RGB values into a single color integer
	 *
	 * @param color
	 *            RGB color integer
	 * @param degrees
	 *            degree of hue rotation 0 to 360
	 * @param saturation
	 *            saturation from -1.0 to 1.0, where 0 is no effect
	 * @param lightness
	 *            lightness from -1.0 to 1.0, where 0 is no effect
	 *
	 * @return RGB color that has been manipulated through the HSL space
	 */
	public static int modifyHSL(int color, int degrees, double saturation, double lightness) {

		double h = 0, s, l;

		double r = red(color) / 255.0;
		double g = green(color) / 255.0;
		double b = blue(color) / 255.0;

		double min = Math.min(r, Math.min(g, b));
		double max = Math.max(r, Math.max(g, b));
		double deltaMax = max - min;

		double rotation = degrees / 360.0;

		l = (max + min) / 2;

		// Conversion from RGB to HSL

		if (deltaMax == 0) {
			h = 0;
			s = 0;
		} else {
			s = l < 0.5 ? deltaMax / (max + min) : deltaMax / (2 - max - min);

			double delR, delG, delB;
			delR = (((max - r) / 6) + (deltaMax / 2)) / deltaMax;
			delG = (((max - g) / 6) + (deltaMax / 2)) / deltaMax;
			delB = (((max - b) / 6) + (deltaMax / 2)) / deltaMax;

			if (r == max)
				h = delB - delG;
			else if (g == max)
				h = 0.333 + delR - delB;
			else if (b == max)
				h = 0.666 + delG - delR;

			if (h < 0)
				h += 1;
			if (h > 1)
				h -= 1;
		}

		// Affect HSL

		h += rotation;
		if (h < 0)
			h += 1;
		if (h > 1)
			h -= 1;

		s += saturation;
		if (s < 0)
			s += 1;
		if (s > 1)
			s -= 1;

		l += lightness;
		if (l < 0)
			l += 1;
		if (l > 1)
			l -= 1;

		// Conversion back to rgb
		int newR, newG, newB;

		if (s == 0) {

			newR = (int) (l * 255);
			newG = (int) (l * 255);
			newB = (int) (l * 255);

		} else {

			double var2 = l < 0.5 ? l * (1 + s) : (l + s) - (s * l);
			double var1 = 2 * l - var2;

			newR = (int) (255 * hueToRGB(var1, var2, h + 0.333));
			newG = (int) (255 * hueToRGB(var1, var2, h));
			newB = (int) (255 * hueToRGB(var1, var2, h - 0.333));
		}

		return color(255, newR, newG, newB);

	}

	private static double hueToRGB(double v1, double v2, double hue) {
		if (hue < 0)
			hue += 1;
		if (hue > 1)
			hue -= 1;
		if ((6 * hue) < 1)
			return (v1 + (v2 - v1) * 6 * hue);
		if ((2 * hue) < 1)
			return v2;
		if ((3 * hue) < 2)
			return (v1 + (v2 - v1) * (0.666 - hue) * 6);
		return v1;

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
