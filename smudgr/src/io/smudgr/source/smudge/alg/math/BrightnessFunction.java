package io.smudgr.source.smudge.alg.math;

public class BrightnessFunction implements UnivariateFunction {

	public BrightnessFunction() {
		// TODO Auto-generated constructor stub
	}

	public double calculate(double value) {
		int color = (int) value;

		int red = ColorHelper.red(color);
		int blue = ColorHelper.blue(color);
		int green = ColorHelper.green(color);
		
		double brightness = (red + green + blue) / 3;
		return brightness / 255;
	}

}
