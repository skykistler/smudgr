package io.smudgr.source.smudge.alg.coord;

import io.smudgr.source.Frame;
import gnu.trove.list.array.TIntArrayList;
import io.smudgr.source.smudge.alg.bound.Bound;
import io.smudgr.source.smudge.param.NumberParameter;

public class SkewedCoords extends CoordFunction {

	// Which end coordinate do we want
	NumberParameter endCoordOffset = new NumberParameter("Skew Degree", this, 0, 0, 1);
	
	// These arrays will hold the outer perimeter coordinates
	private int[] xs;
	private int[] ys;
	// The following selected coord components will be important later on in the generate method
	private int selectedEndX;
	private int selectedEndY;
	
	private TIntArrayList coordBasis; // The line that we will use to introduce a skew to coords
	
	public String getName() {
		return "Skewed Coords";
	}
	
	protected void generate(Bound b, Frame img) {
		int boundWidth = b.getTranslatedWidth(img);
		int boundHeight = b.getTranslatedHeight(img);
		
		int boundX = b.getTranslatedX(img);
		int boundY = b.getTranslatedY(img);
		
		/* Set the possible end coordinates from the TWO start coords:
		 * 		(0, 0)
		 * 		(0, width-1)
		*/
		int possibleRangeSize = boundHeight + boundWidth + boundHeight - 2;
		xs = new int[possibleRangeSize];
		ys = new int[possibleRangeSize];
		
		fillPossibleEndCoords(boundX, boundY, boundWidth, boundHeight);
		
		TIntArrayList coordBasis = new TIntArrayList();
		
	}

	private void fillPossibleEndCoords(int offsetX, int offsetY, int width, int height) {
		
		// count shouldn't exceed the length of xs and ys but why assert things if I'm always right
		int index = 0;
		
		// We want to follow a very specific order for this to work
		for(int i = 0; i < height - 1; i++) {
			xs[index] = width-1;
			ys[index] = i;
			index++;
		}
		for(int i = 0; i < width - 1; i++) {
			xs[index] = (width - i - 1);
			ys[index] = height - 1;
			index++;
		}
		for(int i = 0; i < height - 1; i++) {
			xs[index] = 0;
			ys[index] = (height - i - 1);
			index++;
		}
		
		// at this point, we have all possible end coordinates separated by x and y
		// I'm also not sure why I separated into x and y
	}

}
