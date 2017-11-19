package io.smudgr.engine.utility.color;

import java.util.stream.IntStream;

import io.smudgr.engine.alg.math.ColorHelper;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.engine.utility.UtilityComponent;
import io.smudgr.util.PixelFrame;

/**
 * The {@link Painter} class defines a {@link UtilityComponent} that can, pixel
 * by
 * pixel, manipulate the colors in an image. It should manipulate the frame
 * before the frame is enlarged or after the frame is shrunken by another
 * {@link UtilityComponent}.
 */
public class Painter extends UtilityComponent {

	@Override
	public String getTypeName() {
		return "Painter";
	}

	@Override
	public String getComponentTypeName() {
		return "Painter";
	}

	@Override
	public String getComponentTypeIdentifier() {
		return "painter";
	}

	NumberParameter saturation = new NumberParameter("Saturation", this, 0.5, -1.0, 1.0, 0.01);
	NumberParameter degree = new NumberParameter("Hue Rotation", this, 0, 0, 359, 1);
	NumberParameter brightness = new NumberParameter("Brightness", this, 0, -1.0, 1.0, 0.01);

	private double sat, val;
	private int deg; // colorSpace;

	/**
	 * Manipulates each pixel of the given Frame given some color related
	 * parameters.
	 * 
	 * @param f
	 *            {@link PixelFrame}
	 */
	public void color(PixelFrame f) {
		sat = saturation.getValue();
		val = brightness.getValue();
		deg = degree.getIntValue();

		if (sat == 0.5 && val == 0 && deg == 0)
			return;

		// After testing, the for loop is definitely slower

		// for (int i = 0; i < f.pixels.length; i++) {
		// f.pixels[i] = manipulate(f.pixels[i]);
		// }

		IntStream.range(0, f.pixels.length)
				.parallel()
				.forEach(i -> f.pixels[i] = manipulate(f.pixels[i]));
	}

	private int manipulate(int pixel) {
		return ColorHelper.modifyHSV(pixel, deg, sat, val);
	}

}
