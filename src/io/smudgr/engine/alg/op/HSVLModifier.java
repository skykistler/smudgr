package io.smudgr.engine.alg.op;

import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.alg.math.ColorHelper;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.util.Frame;

/**
 * HSV/L Modifier provides a traditional manipulation for saturation, hue, and
 * brightness.
 */
public class HSVLModifier extends ParallelOperation {

	@Override
	public String getTypeName() {
		return "HSV/HSL Modifier";
	}

	NumberParameter saturation = new NumberParameter("Saturation", this, 0.5, -1.0, 1.0, 0.01);
	NumberParameter degree = new NumberParameter("Hue Rotation", this, 0, 0, 359, 1);
	NumberParameter value = new NumberParameter("Value/Lightness", this, 0, -1.0, 1.0, 0.01);
	NumberParameter type = new NumberParameter("Color Space", this, 1, 1, 2, 1);

	private double sat, val;
	private int deg, colorSpace, index, coord;

	@Override
	public void preParallel(Frame img) {
		sat = saturation.getValue();
		val = value.getValue();
		deg = degree.getIntValue();
		colorSpace = type.getIntValue();
	}

	@Override
	public ParallelOperationTask getParallelTask() {
		return new HSVLTask();
	}

	class HSVLTask extends ParallelOperationTask {

		@Override
		public void executeParallel(Frame img, PixelIndexList coords) {
			for (index = 0; index < coords.size(); index++) {
				coord = coords.get(index);
				img.pixels[coord] = manipulate(img.pixels[coord], deg, sat, val);
			}
		}

		private int manipulate(int color, int deg, double sat, double val) {
			switch (colorSpace) {
				case 1:
					return ColorHelper.modifyHSV(color, deg, sat, val);
				case 2:
					return ColorHelper.modifyHSL(color, deg, sat, val);
				default:
					return ColorHelper.modifyHSL(color, deg, sat, val);
			}
		}
	}

}
