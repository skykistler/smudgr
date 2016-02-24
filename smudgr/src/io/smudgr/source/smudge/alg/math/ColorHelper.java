package io.smudgr.source.smudge.alg.math;

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
	
	
	public static int modifyHSV(int color, int degrees, double saturation, double value) {
		double r = red(color)/255.0;
		double g = green(color)/255.0;
		double b = blue(color)/255.0;
		
		double h = 0, s, v;
		
		double rotation = degrees/ 360.0;
		
		double min = Math.min(r, Math.min(g,  b));
		double max = Math.max(r, Math.max(g, b));
		double deltaMax = max - min;
		
		v = max;
		
		if(deltaMax == 0) {
			h = 0;
			s = 0;
		} else {
			s = deltaMax / max;
			
			double delR, delG, delB;
			delR = ( ( ( max - r ) / 6)  + ( deltaMax / 2) ) / deltaMax;
			delG = ( ( ( max - g ) / 6)  + ( deltaMax / 2) ) / deltaMax;
			delB = ( ( ( max - b ) / 6)  + ( deltaMax / 2) ) / deltaMax;
			
			if(r == max) 
				h = delB - delG;
			else if(g == max) 
				h = 0.333 + delR - delB;
			else if(b == max) 
				h = 0.666 + delG - delR;
			
			if(h < 0) h += 1;
			if(h > 1) h -= 1;
			
		}
		
		// This part affects HSV
		h += rotation;
		if(h < 0) h += 1;
		if(h > 1) h -= 1;
		
		s += saturation;
		if(s < 0) s += 1;
		if(s > 1) s -= 1;
		
		v += value;
		if(v < 0) v += 1;
		if(v > 1) v -= 1;
		
		// This last part converts back to RGB
		double newR, newG, newB;
		
		if(s == 0) {
			newR = v;
			newG = v;
			newB = v;
		} else {
			double varH, var1, var2, var3;
			int varI;
			
			
			varH = h * 6;
			if(varH == 6) varH = 0;
			varI = (int) varH;
			var1 = (v * (1 - s));
			var2 = (v * (1 - s * (varH - varI) ));
			var3 = (v * (1 - s * (1 - (varH - varI))));
			
			
			switch(varI) {
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
	 * 			  lightness from -1.0 to 1.0, where 0 is no effect
	 * 
	 * @return RGB color that has been manipulated through the HSL space
	 */
	public static int modifyHSL(int color, int degrees, double saturation, double lightness) {
		
		double h = 0, s, l;
		
		double r = red(color)/255.0;
		double g = green(color)/255.0;
		double b = blue(color)/255.0;
		
		double min = Math.min(r, Math.min(g,  b));
		double max = Math.max(r, Math.max(g, b));
		double deltaMax = max - min;
		
		double rotation = degrees/ 360.0;
		
		l = (max + min) / 2;
		
		// Conversion from RGB to HSL
		
		if(deltaMax == 0) {
			h = 0;
			s = 0;
		} else {
			s = l < 0.5 ? deltaMax / (max + min) : deltaMax / (2 - max - min);
			
			double delR, delG, delB;
			delR = ( ( ( max - r ) / 6)  + ( deltaMax / 2) ) / deltaMax;
			delG = ( ( ( max - g ) / 6)  + ( deltaMax / 2) ) / deltaMax;
			delB = ( ( ( max - b ) / 6)  + ( deltaMax / 2) ) / deltaMax;
			
			if(r == max ) 
				h = delB - delG;
			else if(g == max) 
				h = 0.333 + delR - delB;
			else if(b == max) 
				h = 0.666 + delG - delR;
			
			if(h < 0) h += 1;
			if(h > 1) h -= 1;
		}
		
		// Affect HSL
		
		h += rotation;
		if(h < 0) h += 1;
		if(h > 1) h -= 1;
		
		s += saturation;
		if(s < 0) s += 1;
		if(s > 1) s -= 1;
		
		l += lightness;
		if(l < 0) l += 1;
		if(l > 1) l -= 1;
		
		// Conversion back to rgb
		int newR, newG, newB;
		
		if(s == 0) {
			
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
		if(hue < 0)
			hue += 1;
		if(hue > 1)
			hue -= 1;
		if(( 6 * hue) < 1) 
			return (v1 + ( v2 - v1) * 6 * hue);
		if(( 2 * hue) < 1)
			return v2;
		if(( 3 * hue ) < 2)
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
