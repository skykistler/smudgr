package io.smudgr.source.smudge.alg.coord;

import io.smudgr.source.Frame;
import gnu.trove.list.array.TIntArrayList;
import io.smudgr.source.smudge.alg.bound.Bound;
import io.smudgr.source.smudge.param.NumberParameter;

public class SkewedCoords extends CoordFunction {

	// Which end coordinate do we want
	NumberParameter endCoordOffset = new NumberParameter("Skew Degree", this, 0, 0, 2.0);
	
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
		
		double offset = endCoordOffset.getValue();
		
		/* Set the possible end coordinates from the TWO start coords:
		 * 		(0, 0)
		 * 		(0, width-1)
		*/
		int possibleRangeSize = boundHeight + boundWidth + boundHeight - 2;
		xs = new int[possibleRangeSize];
		ys = new int[possibleRangeSize];
		
		fillPossibleEndCoords(boundX, boundY, boundWidth, boundHeight);
		
		selectEndPoint(boundWidth, boundHeight, offset);
		// Now that we have all possible end coordinates, we can move on to creating the coordinate basis
		
		TIntArrayList coordBasis = new TIntArrayList();
		
		createCoordBasis(selectedEndX, selectedEndY, offset, boundWidth);
		
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
		for(int i = 0; i < width; i++) {
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
		// I'm also not sure why I separated x and y into separte arrays, but it looks better
	}
	
	// returns true if we should use start point 1, and false if start point 2
	private void selectEndPoint(int w, int h, double offset) {
	// compute our end x and y for the coord basis this should probably be its own thing
		int length = w + h - 2;
		int index = (int) (length * offset);
		
		if (offset > 1) 
			index += h; 
		
		selectedEndX = xs[index];
		selectedEndY = ys[index];
		
	}
	
	private void createCoordBasis(int endX, int endY, double offset, int width) {
		// Now we just need to compute bresenham, based on what endX and endY are...
		// if the x component of the end point is 0, we need to use the start point 2 to start
		int startX = 0;
		int startY = 0;
		
		if (offset > 1)
			startX = width-1;
		
	}
	
	public void bresenham(int x1, int y1, int x2, int y2) {
		int w = x2 - x1;
		int h = y2 - y1;
		int dx1 = 0;
		int dy1 = 0;
		int dx2 = 0;
		int dy2 = 0;

		if (w < 0)
			dx1 = -1;
		else if (w > 0)
			dx1 = 1;
		if (h < 0)
			dy1 = -1;
		else if (h > 0)
			dy1 = 1;
		if (w < 0)
			dx2 = -1;
		else if (w > 0)
			dx2 = 1;

		int longest = Math.abs(w);
		int shortest = Math.abs(h);
		if (!(longest > shortest)) {
			longest = Math.abs(h);
			shortest = Math.abs(w);
			if (h < 0)
				dy2 = -1;
			else if (h > 0)
				dy2 = 1;
			dx2 = 0;
		}
		int numerator = longest >> 1;
		for (int i = 0; i <= longest; i++) {
			nextPoint(x1, y1);
			numerator += shortest;
			if (!(numerator < longest)) {
				numerator -= longest;
				x1 += dx1;
				y1 += dy1;
			} else {
				x1 += dx2;
				y1 += dy2;
			}
		}
		
		nextSet();
	}


}
