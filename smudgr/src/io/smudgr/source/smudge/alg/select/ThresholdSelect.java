package io.smudgr.source.smudge.alg.select;

import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.math.LumaFunction;
import io.smudgr.source.smudge.alg.math.UnivariateFunction;
import io.smudgr.source.smudge.param.NumberParameter;

public class ThresholdSelect extends Selector {

	NumberParameter threshold = new NumberParameter("Threshold", this, .5, -.01, 1, .01);
	UnivariateFunction thresholdFunc = new LumaFunction();

	public String getName() {
		return "Threshold Select";
	}

	public void init() {
		threshold.setReverse(true);
	}

	public boolean selectsPoint(Frame img, int x, int y) {
		return thresholdFunc.calculate(img.get(x, y)) > threshold.getValue();
	}

}
