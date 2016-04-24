package io.smudgr.project.smudge.alg.select;

import io.smudgr.project.smudge.alg.math.ChromaFunction;
import io.smudgr.project.smudge.alg.math.HueFunction;
import io.smudgr.project.smudge.alg.math.LogFunction;
import io.smudgr.project.smudge.alg.math.LumaFunction;
import io.smudgr.project.smudge.alg.math.SinFunction;
import io.smudgr.project.smudge.param.BooleanParameter;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.project.smudge.param.UnivariateParameter;
import io.smudgr.project.smudge.util.Frame;

public class RangeSelect extends Selector {

	NumberParameter min = new NumberParameter("Minimum Value", this, 0, 0, 1, .01);
	NumberParameter range = new NumberParameter("Range Length", this, .5, 0, 1, .01);
	BooleanParameter wrap = new BooleanParameter("Wrap Range", this, true);

	UnivariateParameter function = new UnivariateParameter("Function", this, new LumaFunction());

	public String getName() {
		return "Range Selection";
	}

	public void init() {
		min.setContinuous(true);

		function.add(new ChromaFunction());
		function.add(new HueFunction());
		function.add(new SinFunction());
		function.add(new LogFunction());
	}

	public boolean selectsPoint(Frame img, int x, int y) {
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
