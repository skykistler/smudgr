package me.skykistler.smudgr.alg;

import java.util.Arrays;
import java.util.Comparator;

import me.skykistler.smudgr.alg.param.BooleanParameter;
import me.skykistler.smudgr.alg.param.DoubleParameter;
import me.skykistler.smudgr.view.View;
import processing.core.PImage;

public class LumaSort extends Algorithm {

	BooleanParameter sortRows = new BooleanParameter(this, "Sort Rows", true);
	BooleanParameter sortColumns = new BooleanParameter(this, "Sort Columns", true);
	BooleanParameter reverseColumns = new BooleanParameter(this, "Reverse Sort Columns", true);
	BooleanParameter reverseRows = new BooleanParameter(this, "Reverse Sort Rows", true);
	DoubleParameter lumaThreshX = new DoubleParameter(this, "Luma Threshold X", 127, -1, 256, 1);
	DoubleParameter lumaThreshY = new DoubleParameter(this, "Luma Threshold Y", 127, -1, 256, 1);
	DoubleParameter rowStart = new DoubleParameter(this, "Starting Row Bound", 0, 0, 1);
	DoubleParameter rowEnd = new DoubleParameter(this, "Ending Row Bound", 1, 0, 1);
	DoubleParameter columnStart = new DoubleParameter(this, "Starting Column Bound", 0, 0, 1);
	DoubleParameter columnEnd = new DoubleParameter(this, "Ending Column Bound", 1, 0, 1);

	int loops = 1;

	int row_start = 0;
	int row_end = 0;
	int column_start = 0;
	int column_end = 0;

	PImage img = null;
	View processor = null;

	public String getName() {
		return "Luma Sort";
	}

	public void execute(View processor, PImage img) {
		this.processor = processor;

		this.img = img;

		row_start = (int) (rowStart.getValue() * img.height);
		row_end = (int) (rowEnd.getValue() * img.height);

		column_start = (int) (columnStart.getValue() * img.width);
		column_end = (int) (columnEnd.getValue() * img.width);

		for (int i = 0; i < loops; i++) {
			if (sortColumns.getValue())
				while (column_start < column_end - 1) {
					sortColumn();
					column_start++;
				}

			column_start = (int) (columnStart.getValue() * img.width);
			if (sortRows.getValue())
				while (row_start < row_end - 1) {
					sortRow();
					row_start++;
				}
		}
	}

	public void sortRow() {
		int x = column_start;
		int y = row_start;
		int xend = 0;

		while (xend < column_end - 1) {
			x = getFirstNotLumaX(x, y);
			xend = getNextLumaX(x, y);

			if (x < 0)
				break; 

			int sortLength = xend - x;

			Integer[] toSort = new Integer[sortLength];

			for (int i = 0; i < sortLength; i++) {
				toSort[i] = img.pixels[x + i + y * img.width];
			}

			Arrays.sort(toSort, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					float o1l = luma((int) o1);
					float o2l = luma((int) o2);
					if (reverseRows.getValue() ) {
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
				img.pixels[x + i + y * img.width] = (int) toSort[i];
			}

			x = xend + 1;
		}
	}

	public void sortColumn() {
		int x = column_start;
		int y = row_start;
		int yend = 0;

		while (yend < row_end - 1) {
			y = getFirstNotLumaY(x, y);
			yend = getNextLumaY(x, y);

			if (y < 0)
				break;

			int sortLength = yend - y;

			Integer[] toSort = new Integer[sortLength];

			for (int i = 0; i < sortLength; i++) {
				toSort[i] = img.pixels[x + (i + y) * img.width];
			}

			Arrays.sort(toSort, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					float o1l = luma((int) o1);
					float o2l = luma((int) o2);
					if (reverseColumns.getValue() ) {
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
				img.pixels[x + (i + y) * img.width] = (int) toSort[i];
			}

			y = yend + 1;
		}
	}

	// LUMA
	int getFirstNotLumaX(int x, int y) {
		while (luma(img.pixels[x + y * img.width]) < lumaThreshX.getValue()) {
			x++;
			if (x >= column_end)
				return -1;
		}
		return x;
	}

	int getNextLumaX(int _x, int y) {
		int x = _x + 1;
		while (luma(img.pixels[x + y * img.width]) > lumaThreshX.getValue()) {
			x++;
			if (x >= column_end)
				return column_end - 1;
		}
		return x - 1;
	}

	// LUMA
	int getFirstNotLumaY(int x, int y) {
		if (y < row_end) {
			while (luma(img.pixels[x + y * img.width]) < lumaThreshY.getValue()) {
				y++;
				if (y >= row_end)
					return -1;
			}
		}
		return y;
	}

	int getNextLumaY(int x, int _y) {
		int y = _y + 1;
		if (y < row_end) {
			while (luma(img.pixels[x + y * img.width]) > lumaThreshY.getValue()) {
				y++;
				if (y >= row_end)
					return row_end - 1;
			}
		}
		return y - 1;
	}
	
	float luma(int color) {
		float red = processor.red(color);
		float blue = processor.blue(color);
		float green = processor.green(color);

		return .299f * red + .587f * green + .114f * blue;
	}
}
