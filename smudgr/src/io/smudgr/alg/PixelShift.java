package io.smudgr.alg;

import java.util.ArrayList;

import io.smudgr.Smudge;
import io.smudgr.alg.coord.ColumnCoords;
import io.smudgr.alg.coord.CoordFunction;
import io.smudgr.alg.math.LinearFunction;
import io.smudgr.alg.math.UnivariateFunction;
import io.smudgr.alg.param.NumberParameter;
import io.smudgr.source.Frame;

public class PixelShift extends Algorithm {

	private NumberParameter amount = new NumberParameter(this, "Amount", 0, 0, 1, 0.005);
	private NumberParameter intervals = new NumberParameter(this, "Intervals", 5, 1, 1000, 1); // Change to be a percentage or not I guess
	//private NumberParameter intervals = new NumberParameter(this, "Intervals", 0.25, 0, 0.51, 0.001);
	//private NumberParameter start = new NumberParameter(this, "Start", 0.4, 0, 1, 0.001);
	//private NumberParameter end = new NumberParameter(this, "End", 0.55, 0, 1, 0.001);
	private NumberParameter shiftType = new NumberParameter(this, "Shift Type", 0, 0, 2, 1); // allows shift to be set via switch structure in calculateShift function
	
	
	UnivariateFunction scale = new LinearFunction();

	Frame orig;
	Frame shifted;

	public PixelShift(Smudge s) {
		super(s);
		amount.setContinuous(true);
		//start.setContinuous(true);
		//end.setContinuous(true);
		setCoordFunction(new ColumnCoords());
		
	}

	public void init() {
		super.init();
	}

	public void execute(Frame img) {
		int size = getCoordFunction().getCoordSet().size();
		
		//if (intervals.getMax() != size)
		//	intervals.setMax(size);

		orig = img.copy();
		shifted = img.copy();

		CoordFunction cf = getCoordFunction();
		double shift = amount.getValue();
		
//		if (intervalWidth < 1) 
//			intervalWidth = 1;
//		else if (intervalWidth > size/2) 
//			intervalWidth = size;
		
		double ints = intervals.getValue();
		
		double intervalWidth = size / ints;
		
		if(intervalWidth < 1) {
			intervalWidth = 1;
			ints = size;
		}
		
		//int n0 = (int) (start.getValue() * ints);
		//int n1 = (int) (end.getValue() * ints);
		
		for (int n = 1; n < ints + 1; n++) {
			
			double interval = (n - 1) * intervalWidth;
			
			//if (inStopArea(n, n0, n1, img.getWidth())) continue;
			
			for (int i = 0; i < intervalWidth; i++) {
				int index = (int) (interval + i);
				if (index < cf.getCoordSet().size()) {
					ArrayList<Integer> coords = cf.getCoordSet().get(index);
					shift(coords, n * shift);
				}
			}
		}

		img.pixels = shifted.pixels;
	}
	
	public void shift(ArrayList<Integer> coords, double amount) {
		for (int i = 0; i < coords.size(); i++) {
			int shift = (int) (i + amount * coords.size());
			shift %= coords.size();

			shifted.pixels[coords.get(i)] = orig.pixels[coords.get(shift)];
		}
	}

	public boolean inStopArea(int n, int n0, int n1, int width){
		if( n0 <= n1) 
			return (n <= n1 && n >= n0);
		else
			return ( (n >= 0 && n <= n1) || (n >= n0 && n <= width) );
		
	}
	
	public String getName() {
		return "Pixel Shift";
	}

}
