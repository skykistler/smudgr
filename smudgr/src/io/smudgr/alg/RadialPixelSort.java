package io.smudgr.alg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import io.smudgr.alg.param.BooleanParameter;
import io.smudgr.alg.param.DoubleParameter;
import io.smudgr.view.View;
import processing.core.PImage;

public class RadialPixelSort extends Algorithm {

	DoubleParameter innerRadius = new DoubleParameter(this, "Inner Radius", 0, 0, 1, 0.1);
	BooleanParameter reverse = new BooleanParameter(this, "Reverse Sort", true);
	DoubleParameter lumaThresh = new DoubleParameter(this, "Luma Threshold", 127, -1, 256, 1);
	DoubleParameter rowStart = new DoubleParameter(this, "Starting Row Bound", 0.4, 0, 1);
	DoubleParameter rowEnd = new DoubleParameter(this, "Ending Row Bound", 0.6, 0, 1);
	DoubleParameter columnStart = new DoubleParameter(this, "Starting Column Bound", 0.4, 0, 1);
	DoubleParameter columnEnd = new DoubleParameter(this, "Ending Column Bound", 0.6, 0, 1);
	DoubleParameter degree = new DoubleParameter(this, "Degree of Rotation", 0, 0, 360, 1);
	
	int loops = 1;

	int row_start = 0;
	int row_end = 0;
	int column_start = 0;
	int column_end = 0;
	int radiusX = 0;
	int radiusY = 0;
	int centerY = 0;
	int centerX = 0;
	
	double sin = 0;
	double cos = 0;
	double tan = 0;
	
	ArrayList<Integer> coords; // = new ArrayList<Integer>();

	PImage img = null;
	View processor = null;

	public void execute(View processor, PImage img) {
		this.processor = processor;

		this.img = img;
				
		row_start = (int) (rowStart.getValue() * img.height) + 1;
		row_end = (int) (rowEnd.getValue() * img.height) - 1;

		column_start = (int) (columnStart.getValue() * img.width) + 1;
		column_end = (int) (columnEnd.getValue() * img.width) - 1;
		
		centerY = row_start + (row_end - row_start)/2;
		centerX = column_start + (column_end - column_start)/2;

		radiusY = (row_end - row_start)/2;
		radiusX = (column_end - column_start)/2;
		
		int innerR = (int) ( radiusY >=  radiusX ? radiusX - (int)radiusX*innerRadius.getValue() : radiusY - (int)radiusY*innerRadius.getValue());
		
		int _radiusY = radiusY;
		int _radiusX = radiusX;
		for(int i = 0; i < innerR; i++) {
			coords = bresenham(_radiusX, _radiusY);
			//Collections.sort(coords);
			sort();
			_radiusX--;
			_radiusY--;
		}
	}
	
	//This function will return a list of coordinates
	public ArrayList<Integer> bresenham(int radiusX, int radiusY) {
		
		ArrayList<Integer> coords = new ArrayList<Integer>();
		
		ArrayList<Integer> c1 = new ArrayList<Integer>();
		ArrayList<Integer> c2 = new ArrayList<Integer>();
		ArrayList<Integer> c3 = new ArrayList<Integer>();
		ArrayList<Integer> c4 = new ArrayList<Integer>();
		ArrayList<Integer> c5 = new ArrayList<Integer>();
		ArrayList<Integer> c6 = new ArrayList<Integer>();
		ArrayList<Integer> c7 = new ArrayList<Integer>();
		ArrayList<Integer> c8 = new ArrayList<Integer>();
		
		//first set of points
		int x, y;
		
		int twoASquare = 2 * radiusX * radiusX;
		int twoBSquare = 2 * radiusY * radiusY;
		int dx = radiusY*radiusY*(1 - 2*radiusX);
		int dy = radiusX*radiusX;
		int error = 0;
		int stoppingX = twoBSquare*radiusX;
		int stoppingY = 0;
		
		x = radiusX;
		y = 0;
		while (stoppingX >= stoppingY) {
//			coords.add( (centerX+x) + ((centerY+y)*img.width) );
//			coords.add( (centerX-1+x) + ((centerY-1+y)*img.width) );
//			coords.add( (centerX-2+x) + ((centerY-2+y)*img.width) );
//			coords.add( (centerX-x) + ((centerY+y)*img.width) );
//			coords.add( (centerX-1-x) + ((centerY-1+y)*img.width) );
//			coords.add( (centerX-2-x) + ((centerY-2+y)*img.width) );
//			coords.add( (centerX-x) + ((centerY-y)*img.width) );
//			coords.add( (centerX-1-x) + ((centerY-1-y)*img.width) );
//			coords.add( (centerX-2-x) + ((centerY-2-y)*img.width) );
//			coords.add( (centerX+x) + ((centerY-y)*img.width) );
//			coords.add( (centerX-1+x) + ((centerY-1-y)*img.width) );
//			coords.add( (centerX-2+x) + ((centerY-2-y)*img.width) );
			c1.add( (centerX+x) + ((centerY+y)*img.width) );
			c4.add( 0, (centerX-x) + ((centerY+y)*img.width) );
			c5.add( (centerX-x) + ((centerY-y)*img.width) );
			c8.add( 0, (centerX+x) + ((centerY-y)*img.width) );
			
			y++;
			stoppingY += twoASquare;
			error += dy;
			dy += twoASquare;
			if( (2*error + dx) > 0) {
				x--;
				stoppingX -= twoBSquare;
				error += dx;
				dx += twoBSquare;
			}
		}
		
		//second set of points
		
		dx = radiusY * radiusY;
		dy = radiusX * radiusX * (1 - 2*radiusY);
		error = 0;
		stoppingX = 0;
		stoppingY = twoASquare * radiusY;
		x = 0;
		y = radiusY;
		while( stoppingX <= stoppingY) {
			c2.add( 0, (centerX+x) + ((centerY+y)*img.width) );
			c3.add( (centerX-x) + ((centerY+y)*img.width) );
			c6.add( 0, (centerX-x) + ((centerY-y)*img.width) );
			c7.add( (centerX+x) + ((centerY-y)*img.width) );
			
			
			x++;
			stoppingX += twoBSquare;
			error += dx;
			dx += twoBSquare;
			if( (2*error + dy) > 0) {
				y--;
				stoppingY -= twoASquare;
				error += dy;
				dy += twoASquare;
			}
		}
		
		coords.addAll(c1);
		coords.addAll(c2);
		coords.addAll(c3);
		coords.addAll(c4);
		coords.addAll(c5);
		coords.addAll(c6);
		coords.addAll(c7);
		coords.addAll(c8);
		
		return coords;
		
	}
	
	public void sort() {
		int start = 0;
		int end = 0;

		while (end < coords.size() - 1) {
			start = getFirstNotLuma(start);
			
			if (start < 0) break;
			
			end = getNextLuma(start);

			int sortLength = end - start;

			Integer[] toSort = new Integer[sortLength];

			for (int i = 0; i < sortLength; i++) {
				toSort[i] = img.pixels[coords.get(start+i)];
			}

			Arrays.sort(toSort, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					float o1l = luma((int) o1);
					float o2l = luma((int) o2);
					if (reverse.getValue() ) {
						if (o1l > o2l)
							return 1;
						if (o1l < o2l)
							return -1;
					} else {
						if (o1l < o2l)
							return 1;
						if (o1l > o2l)
							return -1;
					}
					return 0;
				}
			});

			for (int i = 0; i < sortLength; i++) {
				img.pixels[coords.get(start+i)] = (int) toSort[i];
			}
			
			start = end + 1;
		}
	}

	int getFirstNotLuma(int start) {
		int run = start;
		if(start < coords.size()) {
			while (luma(img.pixels[coords.get(run)]) < lumaThresh.getValue()) {
				run++;
				if (run >= coords.size())
					return -1;
			}
		}
		return run;
	}

	int getNextLuma(int start) {
		int run = start;
		while ( luma(img.pixels[coords.get(run)]) > lumaThresh.getValue() ) {
			run++;
			if (run >= coords.size())
				return coords.size() - 1;
		}
		return run;
	}
	
	float luma(int color) {
		float red = processor.red(color);
		float blue = processor.blue(color);
		float green = processor.green(color);

		return .299f * red + .587f * green + .114f * blue;
	}
	
	int rotateX(int x, int y) {
		int dx= (int) (x - centerX);
		int dy = (int) (y - centerY);
		double firstX = Math.round(dx - dy * tan + centerX);
		double firstY = Math.round(dy + centerY);
		dx = (int)(firstX - centerX);
		dy = (int)(firstY - centerY);
		double secondX = Math.round(dx + centerX);
		double secondY = Math.round(sin * dx + dy + centerY);
		dx = (int)(secondX - centerX);
		dy = (int)(secondY - centerY);
		int finalX = (int)(Math.round(dx - dy*tan + centerX));
		//int finalY = (int)(Math.round(dy + centerY));
		
		return finalX;
	}
	
	int rotateY(int x, int y) {
		int dx= (int) (x - centerX);
		int dy = (int) (y - centerY);
		double firstX = Math.round(dx - dy * tan + centerX);
		double firstY = Math.round(dy + centerY);
		dx = (int)(firstX - centerX);
		dy = (int)(firstY - centerY);
		double secondX = Math.round(dx + centerX);
		double secondY = Math.round(sin * dx + dy + centerY);
		dx = (int)(secondX - centerX);
		dy = (int)(secondY - centerY);
		//int finalX = (int)(Math.round(dx - dy*tan + centerX));
		int finalY = (int)(Math.round(dy + centerY));
		return finalY;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Radial Pixel Sort";
	}

	
}
