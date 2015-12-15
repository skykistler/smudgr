package me.skykistler.smudgr.alg;

import java.util.Arrays;
import java.util.Comparator;

import me.skykistler.smudgr.alg.param.BooleanParameter;
import me.skykistler.smudgr.alg.param.DoubleParameter;
import me.skykistler.smudgr.alg.param.IntegerParameter;
import me.skykistler.smudgr.view.View;
import processing.core.PImage;

public class LumaSort extends Algorithm {

	BooleanParameter sortRows = new BooleanParameter(this, "Sort Rows", true);
	BooleanParameter sortColumns = new BooleanParameter(this, "Sort Columns", true);
	DoubleParameter lumaThreshX = new DoubleParameter(this, "Luma Threshold X", 127, -1, 256, 1);
	DoubleParameter lumaThreshY = new DoubleParameter(this, "Luma Threshold Y", 127, -1, 256, 1);
	IntegerParameter rowStart = new IntegerParameter(this, "Starting Row Bound", 0);
	IntegerParameter rowEnd = new IntegerParameter(this, "Ending Row Bound", 1);
	IntegerParameter columnStart = new IntegerParameter(this, "Starting Column Bound", 0);
	IntegerParameter columnEnd = new IntegerParameter(this, "Ending Column Bound", 1);

	int loops = 1;

	int row = 0;
	int column = 0;

	int width = 0;
	int height = 0;

	PImage img = null;
	View processor = null;

	public String getName() {
		return "Luma Sort";
	}

	public void execute(View processor, PImage img) {
		this.processor = processor;

		this.img = img;
		width = img.width;
		height = img.height;

		//Prevents negative array access by flipping values if in backwards order
		//Not really sure if this is a good solution though when using it in real-time...
		if (rowStart.getValue() > rowEnd.getValue()) {
			int temp = rowStart.getValue();
			rowStart.setValue(rowEnd.getValue());
			rowEnd.setValue(temp);
		}
		if (columnStart.getValue() > columnEnd.getValue()) {
			int temp = columnStart.getValue();
			columnStart.setValue(columnEnd.getValue());
			columnEnd.setValue(temp);
		}

		if (rowStart.getValue() > img.height)
			rowStart.setValue(img.height);
		if (rowStart.getValue() < 0)
			rowStart.setValue(0);
		if (rowEnd.getValue() > img.height)
			rowEnd.setValue(img.height);
		if (rowEnd.getValue() < 1)
			rowEnd.setValue(1);

		if (columnStart.getValue() > img.width)
			columnStart.setValue(img.width);
		if (columnStart.getValue() < 0)
			columnStart.setValue(0);
		if (columnEnd.getValue() > img.width)
			columnEnd.setValue(img.width);
		if (columnEnd.getValue() < 1)
			columnEnd.setValue(1);

		row = rowStart.getValue();
		column = columnStart.getValue();

		width = columnEnd.getValue();
		height = rowEnd.getValue();

		for (int i = 0; i < loops; i++) {
			if (sortColumns.getValue())
				while (column < width - 1) {
					sortColumn();
					column++;
				}

			if (sortRows.getValue())
				while (row < height - 1) {
					sortRow();
					row++;
				}
		}
	}

	public void sortRow() {
		int x = column;
		int y = row;
		int xend = 0;

		while (xend < width - 1) {
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
					if (o1l > o2l)
						return 1;
					if (o1l < o2l)
						return -1;
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
		int x = column;
		int y = row;
		int yend = 0;

		while (yend < height - 1) {
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
					if (o1l > o2l)
						return 1;
					if (o1l < o2l)
						return -1;
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
			if (x >= width)
				return -1;
		}
		return x;
	}

	int getNextLumaX(int _x, int y) {
		int x = _x + 1;
		while (luma(img.pixels[x + y * img.width]) > lumaThreshX.getValue()) {
			x++;
			if (x >= width)
				return width - 1;
		}
		return x - 1;
	}

	// LUMA
	int getFirstNotLumaY(int x, int y) {
		if (y < height) {
			while (luma(img.pixels[x + y * img.width]) < lumaThreshY.getValue()) {
				y++;
				if (y >= height)
					return -1;
			}
		}
		return y;
	}

	int getNextLumaY(int x, int _y) {
		int y = _y + 1;
		if (y < height) {
			while (luma(img.pixels[x + y * img.width]) > lumaThreshY.getValue()) {
				y++;
				if (y >= height)
					return height - 1;
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
