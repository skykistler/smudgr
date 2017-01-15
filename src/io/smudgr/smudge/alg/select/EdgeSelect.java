package io.smudgr.smudge.alg.select;

import io.smudgr.smudge.alg.math.univariate.ChromaFunction;
import io.smudgr.smudge.alg.math.univariate.HueFunction;
import io.smudgr.smudge.alg.math.univariate.LogFunction;
import io.smudgr.smudge.alg.math.univariate.LumaFunction;
import io.smudgr.smudge.alg.math.univariate.SinFunction;
import io.smudgr.smudge.alg.math.univariate.UnivariateFunction;
import io.smudgr.smudge.param.NumberParameter;
import io.smudgr.smudge.param.UnivariateParameter;
import io.smudgr.util.Frame;

public class EdgeSelect extends Selector {

	public String getName() {
		return "Edges";
	}

	NumberParameter direction = new NumberParameter("Direction", this, 1, 1, 3, 1);
	UnivariateParameter function = new UnivariateParameter("Function", this, new LumaFunction());
	NumberParameter max = new NumberParameter("Max Edge Strength", this, 1, 0, 1, 0.01);

	public void init() {
		function.setContinuous(false);
		function.add(new ChromaFunction());
		function.add(new HueFunction());
		function.add(new SinFunction());
		function.add(new LogFunction());
	}

	public boolean selectsPoint(Frame img, int x, int y) {
		int d = direction.getIntValue();
		UnivariateFunction f = function.getValue();

		int height = img.getHeight();
		int width = img.getWidth();

		int xplus1 = Math.floorMod(x + 1, width);
		int xminus1 = Math.floorMod(x - 1, width);
		int yplus1 = Math.floorMod(y + 1, height);
		int yminus1 = Math.floorMod(y - 1, height);

		double middle = f.calculate(img.get(x, y));

		double p0, p1, p2, p3, delta;

		/*
		 * Uses some basic image derivative kernels of the form: case 1: [ 0 0 0
		 * ] [ 1 -2 1 ] [ 0 0 0 ]
		 *
		 * case 2: [ 0 1 0 ] [ 0 -2 0 ] [ 0 1 0 ]
		 *
		 * case 3: [ 0 1 0 ] [ 1 -4 1 ] [ 0 1 0 ]
		 */
		switch (d) {
		case 1:
			p0 = f.calculate(img.get(xplus1, y));
			p1 = f.calculate(img.get(xminus1, y));
			delta = (-2 * middle + p0 + p1) / 2.0;
			break;
		case 2:
			p0 = f.calculate(img.get(x, yplus1));
			p1 = f.calculate(img.get(x, yminus1));
			delta = (-2 * middle + p0 + p1) / 2.0;
			break;
		default:
			p0 = f.calculate(img.get(xplus1, y));
			p1 = f.calculate(img.get(xminus1, y));
			p2 = f.calculate(img.get(x, yplus1));
			p3 = f.calculate(img.get(x, yminus1));
			delta = (-4 * middle + p0 + p1 + p2 + p3) / 4.0;
			break;
		}

		return Math.abs(delta) < max.getValue();

	}

}