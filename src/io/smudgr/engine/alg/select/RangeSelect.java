package io.smudgr.engine.alg.select;

import io.smudgr.engine.alg.math.univariate.ChromaFunction;
import io.smudgr.engine.alg.math.univariate.HueFunction;
import io.smudgr.engine.alg.math.univariate.LogFunction;
import io.smudgr.engine.alg.math.univariate.LumaFunction;
import io.smudgr.engine.alg.math.univariate.SinFunction;
import io.smudgr.engine.param.BooleanParameter;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.engine.param.UnivariateParameter;
import io.smudgr.util.PixelFrame;

/**
 * {@link RangeSelect} selects pixels based on their value within a range of
 * values, as calculated by a configurable function. This is sort of like a
 * movable threshold.
 */
public class RangeSelect extends Selector {

	NumberParameter min = new NumberParameter("Minimum Value", this, 0, 0, 1, .01);
	NumberParameter range = new NumberParameter("Range Length", this, .5, 0, 1, .01);
	BooleanParameter wrap = new BooleanParameter("Wrap Range", this, true);

	UnivariateParameter function = new UnivariateParameter("Function", this, new LumaFunction());

	@Override
	public String getTypeName() {
		return "Range";
	}

	@Override
	public void onInit() {
		min.setContinuous(true);

		function.add(new ChromaFunction());
		function.add(new HueFunction());
		function.add(new SinFunction());
		function.add(new LogFunction());
	}

	@Override
	public void generate() {
		if (range.getIntValue() == 1)
			return;

		super.generate();
	}

	@Override
	public boolean selectsPoint(PixelFrame img, int x, int y) {
		return inRange(img.get(x, y));
	}

	private boolean inRange(int value) {
		double rangeVal = range.getValue();
		double minVal = min.getValue();
		double maxVal = minVal + rangeVal;
		double functionVal = function.getValue().calculate(value);

		boolean firstRange = functionVal >= minVal && functionVal <= Math.min(maxVal, 1);

		if (wrap.getValue()) {
			double wrapVal = maxVal - 1;
			return firstRange || functionVal <= wrapVal;
		} else
			return firstRange;
	}

}
