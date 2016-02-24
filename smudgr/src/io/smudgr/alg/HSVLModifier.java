package io.smudgr.alg;

import java.util.ArrayList;

import io.smudgr.alg.math.ColorHelper;
import io.smudgr.alg.param.NumberParameter;
import io.smudgr.source.Frame;
import io.smudgr.source.Smudge;

public class HSVLModifier extends Algorithm {
	
	NumberParameter saturation = new NumberParameter(this, "Saturation", 0.5, -1.0, 1.0, 0.01);
	NumberParameter degree = new NumberParameter(this, "Hue Rotation", 0, 0, 359, 1);
	NumberParameter value = new NumberParameter(this, "Value/Lightness", 0, -1.0, 1.0, 0.01);
	NumberParameter type = new NumberParameter(this, "Color Space", 1, 1, 2, 1);
	
	double lastSaturation, lastValue;
	int lastDegree;
	Frame lastFrame;
	
	public HSVLModifier(Smudge s) {
		super(s);
	}

	public void execute(Frame img) {
		double sat = saturation.getValue();
		double val = value.getValue();
		int deg = degree.getIntValue();
		
		if(lastSaturation == sat && lastValue == val && lastDegree == deg) {
			img.setBufferedImage(lastFrame.getBufferedImage());
		}
		else {
			for(ArrayList<Integer> coords: getCoordFunction().getCoordSet()) {
				for(Integer coord: coords) {
					img.pixels[coord] = manipulate(img.pixels[coord], deg, sat, val);
				}
			}
		}
		
		lastSaturation = sat;
		lastDegree = deg;
		lastValue = val;
		lastFrame = img.copy();
	}

	private int manipulate(int color, int deg, double sat, double val) { 
		int newRGB;
		switch(type.getIntValue()){
			case 1:
				newRGB = ColorHelper.modifyHSV(color, deg, sat, val);
			case 2:
				newRGB = ColorHelper.modifyHSL(color, deg, sat, val);
			default:
				newRGB = ColorHelper.modifyHSL(color, deg, sat, val);
		}
		return newRGB;
	}
	
	public String getName() {
		return "HSV/HSL Modifier";
	}

}
