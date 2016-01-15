package io.smudgr.alg;

import java.util.ArrayList;

import io.smudgr.Smudge;
import io.smudgr.alg.coord.AllCoords;

import io.smudgr.alg.param.NumberParameter;
import io.smudgr.model.Frame;

public class ChromaShift extends Algorithm {
	
	//where to place the displaced color channels
	NumberParameter layer1X = new NumberParameter(this, "Layer 1 X", 1, 0, 2, 0.01);
	NumberParameter layer1Y = new NumberParameter(this, "Layer 1 Y", 1, 0, 2, 0.01);
	NumberParameter layer2X = new NumberParameter(this, "Layer 2 X", 1, 0, 2, 0.01);
	NumberParameter layer2Y = new NumberParameter(this, "Layer 2 Y", 1, 0, 2, 0.01);
	NumberParameter layer3X = new NumberParameter(this, "Layer 3 X", 1, 0, 2, 0.01);
	NumberParameter layer3Y = new NumberParameter(this, "Layer 3 Y", 1, 0, 2, 0.01);
	
	//BooleanParameter sort = new BooleanParameter(this, "Sort", false);
	//UnivariateFunction function = new LumaFunction();

	Frame canvas;
	
	int boundWidth;
	int boundHeight;

	public ChromaShift(Smudge s) {
		super(s);
	}

	public void init() {
		super.init();

		if (getCoordFunction() == null)
			setCoordFunction(new AllCoords());
	}

	public void execute(Frame img) {
		
		boundWidth = (int) (getBound().getWidth() * img.getWidth()); //+ getBound().getOffsetX() * img.getWidth());
		boundHeight = (int) (getBound().getHeight() * img.getHeight()); // + getBound().getOffsetY() * img.getHeight());
		
		canvas = img.copy();
		
		for (ArrayList<Integer> coords : getCoordFunction().getCoordSet()) {
			int w = img.getWidth();
			int h = img.getHeight();
			boolean inImage;

			for (Integer coord : coords) {
				int x = coord % canvas.getWidth();
				int y = (coord - x)/canvas.getWidth();		
				canvas.set(x, y, 0xFF00000);
			}
			
			for (Integer coord: coords) {
				
				int x = coord % canvas.getWidth();
				int y = (coord - x)/canvas.getWidth();
				
				// Translate Layer 1
				int x1 = (int) (x + ( (layer1X.getValue() - 1) * boundWidth));
				int y1 = (int) (y + ( (layer1Y.getValue() - 1) * boundHeight));
				int color1 = img.pixels[x + y * img.getWidth()] & 0xFFFF0000;
				
				inImage = x1 >= 0 && y1 >= 0 && x1 < w && y1 < h;
				if (inImage)
				canvas.set(x1, y1, color1);
				
				// Translate Layer 2
				int x2 = (int) (x + ( (layer2X.getValue() - 1) * boundWidth));
				int y2 = (int) (y + ( (layer2Y.getValue() - 1) * boundHeight));
				int color2 = img.pixels[x + y * img.getWidth()] & 0xFF00FF00;
				
				inImage = x2 >= 0 && y2 >= 0 && x2 < w && y2 < h;
				if (inImage)
				canvas.set(x2, y2, color2);
				
				// Translate Layer 3
				int x3 = (int) (x + ( (layer2X.getValue() - 1) * boundWidth));
				int y3 = (int) (y + ( (layer2Y.getValue() - 1) * boundHeight));
				int color3 = img.pixels[x + y * img.getWidth()] & 0xFF0000FF;
				
				inImage = x3 >= 0 && y3 >= 0 && x3 < w && y3 < h;
				if (inImage)
				canvas.set(x3, y3, color3);
						
			}
		}
		img.pixels = canvas.pixels;
	}


	@Override
	public String getName() {
		return "Chroma Shift";
	}

}
