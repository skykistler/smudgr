package io.smudgr.alg;

import java.util.ArrayList;

import io.smudgr.Smudge;
import io.smudgr.alg.bound.Bound;
import io.smudgr.alg.coord.AllCoords;
import io.smudgr.alg.coord.CoordFunction;
import io.smudgr.alg.math.ColorHelper;
import io.smudgr.alg.param.NumberParameter;
import io.smudgr.model.Frame;

public class ChromaShift extends Algorithm {
	
	//where to place the displaced color channels
	NumberParameter layer1X = new NumberParameter(this, "Layer 1 X", 0.90, 0, 2, 0.01);
	NumberParameter layer1Y = new NumberParameter(this, "Layer 1 Y", 1, 0, 2, 0.01);
	NumberParameter layer2X = new NumberParameter(this, "Layer 2 X", 0.80, 0, 2, 0.01);
	NumberParameter layer2Y = new NumberParameter(this, "Layer 2 Y", 1, 0, 2, 0.01);
	NumberParameter layer3X = new NumberParameter(this, "Layer 3 X", 0.70, 0, 2, 0.01);
	NumberParameter layer3Y = new NumberParameter(this, "Layer 3 Y", 1, 0, 2, 0.01);
	
	NumberParameter innerOffsetX = new NumberParameter(this, "X Offset", 0, 0, 1, 0.1);
	NumberParameter innerOffsetY = new NumberParameter(this, "Y Offset", 0, 0, 1, 0.1);
	NumberParameter innerWidth = new NumberParameter(this, "Width", 1, 0, 1, 0.1);
	NumberParameter innerHeight = new NumberParameter(this, "Height", 1, 0, 1, 0.1);
	
	//BooleanParameter sort = new BooleanParameter(this, "Sort", false);
	//UnivariateFunction function = new LumaFunction();

	Frame canvas;
	
	int boundWidth;
	int boundHeight;
	
	Bound b;
	
	CoordFunction innerCoords = new AllCoords();
	
	public ChromaShift(Smudge s) {
		super(s);
	}

	public void init() {
		super.init();

		setCoordFunction(new AllCoords());
	}

	public void execute(Frame img) {
		
		b = new Bound(innerWidth.getValue(), (int) innerHeight.getValue());
		b.setOffsetX(innerOffsetX.getValue());
		b.setOffsetY(innerOffsetY.getValue());
		innerCoords.setBound(b);
		innerCoords.setImage(img);
		innerCoords.update();
		
		boundWidth = (int) (b.getWidth() * img.getWidth()); //+ getBound().getOffsetX() * img.getWidth());
		boundHeight = (int) (b.getHeight() * img.getHeight()); // + getBound().getOffsetY() * img.getHeight());
		
		canvas = img.copy();
		
		int layer1Weight = 0xFFFF0000;
		int layer2Weight = 0xFF00FF00;
		int layer3Weight = 0xFF0000FF;
		
		for (ArrayList<Integer> coords : innerCoords.getCoordSet()) {

//			for (Integer coord : coords) {
//				int x = coord % canvas.getWidth();
//				int y = (coord - x)/canvas.getWidth();		
//				canvas.set(x, y, 0xFF00000);
//			}
			
			for (Integer coord: coords) {
				
				int x = coord % canvas.getWidth();
				int y = (coord - x)/canvas.getWidth();
				
				int color = img.get(x, y);
				
				shiftColor(x, y, color, layer1X.getValue(), layer1Y.getValue(), layer1Weight);
				shiftColor(x, y, color, layer2X.getValue(), layer2Y.getValue(), layer2Weight);
				shiftColor(x, y, color, layer3X.getValue(), layer3Y.getValue(), layer3Weight);
				
			}
						
		}
	
		img.pixels = canvas.pixels;
	}

	public void shiftColor(int x, int y, int origColor, double xShift, double yShift, int layerBias) {
		
		
		int x0 = (int) (x + ( (xShift - 1) * boundWidth));
		int y0 = (int) (y + ( (yShift - 1) * boundHeight));
		
		int red = getRed(origColor, layerBias);
		int green = getGreen(origColor, layerBias);
		int blue = getBlue(origColor, layerBias);
		
		int color = ColorHelper.color(255, red, green, blue);
		
		if (getBound().containsPoint(img, x0, y0) ){
			color = canvas.get(x0, y0) | color;
			canvas.set(x0, y0, color);
		}	
	}

	@Override
	public String getName() {
		return "Chroma Shift";
	}
	
	public int getRed(int color, int bias) {
		double red = (( ColorHelper.red(bias) ) / 255);
		int colorsRed = (int) ( ColorHelper.red(color) * red ) ;
		return colorsRed;
	}
	
	public int getGreen(int color, int bias) {
		double green = (( ColorHelper.green(bias) ) / 255);
		int colorsGreen = (int) ( ColorHelper.green(color) * green) ;
		return colorsGreen;
	}
	
	public int getBlue(int color, int bias) {
		double blue = (( ColorHelper.red(bias) ) / 255);
		int colorsBlue = (int) ( ColorHelper.red(color) * blue ) ;
		return colorsBlue;
	}

}
