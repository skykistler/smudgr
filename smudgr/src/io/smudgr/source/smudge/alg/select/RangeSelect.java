package io.smudgr.source.smudge.alg.select;

import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.math.LumaFunction;
import io.smudgr.source.smudge.alg.math.UnivariateFunction;
import io.smudgr.source.smudge.param.NumberParameter;

public class RangeSelect extends Selector {
	
	NumberParameter min = new NumberParameter("Minimum Value", this, 0, -.01, 1, .01);
	NumberParameter range = new NumberParameter("Range Length", this, 1, -.01, 1, .01);
	
	UnivariateFunction rangeFunc = new LumaFunction();
	
	public boolean selectsPoint(Frame img, int x, int y) {
		return inRange(img.get(x, y));
	}

	public String getName() {
		return "Range Select";
	}
	
	private boolean inRange(int value){	
		double minRange = min.getValue();
		double rangeLength = range.getValue();
		
		double resultVal = rangeFunc.calculate(value);
		return resultVal >= minRange && resultVal < Math.min(minRange + rangeLength, 1.0);
	}
	

}
