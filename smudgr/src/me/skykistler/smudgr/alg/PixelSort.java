package me.skykistler.smudgr.alg;

import java.util.Arrays;
import java.util.Comparator;

import me.skykistler.smudgr.view.View;
import processing.core.PImage;

public class PixelSort implements Algorithm {

	public enum SortType {
		BLACK, BRIGHTNESS, WHITE, LUMA
	}

	public PixelSort(SortType type) {
		this.mode = type.ordinal();
	}

	int mode = 0;

	int loops = 1;

	int blackValue = -10000000;
	int brigthnessValue = 60;
	int whiteValue = -6000000;
	float lumaValue = 120;

	int row = 0;
	int column = 0;
	int width = 0;
	int height = 0;

	PImage img = null;
	View processor = null;

	public void execute(View processor, PImage img) {
		this.processor = processor;

		this.img = img;
		width = img.width;
		height = img.height;

		row = 0;
		column = 0;

		for (int i = 0; i < loops; i++) {
			while (column < width - 1) {
				sortColumn();
				column++;
			}

			while (row < height - 1) {
				sortRow();
				row++;
			}
		}
	}

	public void sortRow() {
		int x = 0;
		int y = row;
		int xend = 0;

		while (xend < width - 1) {
			switch (mode) {
			case 0:
				x = getFirstNotBlackX(x, y);
				xend = getNextBlackX(x, y);
				break;
			case 1:
				x = getFirstBrightX(x, y);
				xend = getNextDarkX(x, y);
				break;
			case 2:
				x = getFirstNotWhiteX(x, y);
				xend = getNextWhiteX(x, y);
				break;
			case 3:
				x = getFirstNotLumaX(x, y);
				xend = getNextLumaX(x, y);
				break;
			default:
				break;
			}

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
		int y = 0;
		int yend = 0;

		while (yend < height - 1) {
			switch (mode) {
			case 0:
				y = getFirstNotBlackY(x, y);
				yend = getNextBlackY(x, y);
				break;
			case 1:
				y = getFirstBrightY(x, y);
				yend = getNextDarkY(x, y);
				break;
			case 2:
				y = getFirstNotWhiteY(x, y);
				yend = getNextWhiteY(x, y);
				break;
			case 3:
				y = getFirstNotLumaY(x, y);
				yend = getNextLumaY(x, y);
				break;
			default:
				break;
			}

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

	// BLACK
	public int getFirstNotBlackX(int _x, int _y) {
		int x = _x;
		int y = _y;
		while (img.pixels[x + y * img.width] < blackValue) {
			x++;
			if (x >= width)
				return -1;
		}
		return x;
	}

	int getNextBlackX(int _x, int _y) {
		int x = _x + 1;
		int y = _y;
		while (img.pixels[x + y * img.width] > blackValue) {
			x++;
			if (x >= width)
				return width - 1;
		}
		return x - 1;
	}

	// BRIGHTNESS
	int getFirstBrightX(int _x, int _y) {
		int x = _x;
		int y = _y;
		while (processor.brightness(img.pixels[x + y * img.width]) < brigthnessValue) {
			x++;
			if (x >= width)
				return -1;
		}
		return x;
	}

	int getNextDarkX(int _x, int _y) {
		int x = _x + 1;
		int y = _y;
		while (processor.brightness(img.pixels[x + y * img.width]) > brigthnessValue) {
			x++;
			if (x >= width)
				return width - 1;
		}
		return x - 1;
	}

	// WHITE
	int getFirstNotWhiteX(int _x, int _y) {
		int x = _x;
		int y = _y;
		while (img.pixels[x + y * img.width] > whiteValue) {
			x++;
			if (x >= width)
				return -1;
		}
		return x;
	}

	int getNextWhiteX(int _x, int _y) {
		int x = _x + 1;
		int y = _y;
		while (img.pixels[x + y * img.width] < whiteValue) {
			x++;
			if (x >= width)
				return width - 1;
		}
		return x - 1;
	}

	// LUMA
	int getFirstNotLumaX(int x, int y) {
		while (luma(img.pixels[x + y * img.width]) < lumaValue) {
			x++;
			if (x >= width)
				return -1;
		}
		return x;
	}

	int getNextLumaX(int _x, int y) {
		int x = _x + 1;
		while (luma(img.pixels[x + y * img.width]) > lumaValue) {
			x++;
			if (x >= width)
				return width - 1;
		}
		return x - 1;
	}

	// BLACK
	int getFirstNotBlackY(int _x, int _y) {
		int x = _x;
		int y = _y;
		if (y < height) {
			while (img.pixels[x + y * img.width] < blackValue) {
				y++;
				if (y >= height)
					return -1;
			}
		}
		return y;
	}

	int getNextBlackY(int _x, int _y) {
		int x = _x;
		int y = _y + 1;
		if (y < height) {
			while (img.pixels[x + y * img.width] > blackValue) {
				y++;
				if (y >= height)
					return height - 1;
			}
		}
		return y - 1;
	}

	// BRIGHTNESS
	int getFirstBrightY(int _x, int _y) {
		int x = _x;
		int y = _y;
		if (y < height) {
			while (processor.brightness(img.pixels[x + y * img.width]) < brigthnessValue) {
				y++;
				if (y >= height)
					return -1;
			}
		}
		return y;
	}

	int getNextDarkY(int _x, int _y) {
		int x = _x;
		int y = _y + 1;
		if (y < height) {
			while (processor.brightness(img.pixels[x + y * img.width]) > brigthnessValue) {
				y++;
				if (y >= height)
					return height - 1;
			}
		}
		return y - 1;
	}

	// WHITE
	int getFirstNotWhiteY(int _x, int _y) {
		int x = _x;
		int y = _y;
		if (y < height) {
			while ((img.pixels[x + y * img.width]) > whiteValue) {
				y++;
				if (y >= height)
					return -1;
			}
		}
		return y;
	}

	int getNextWhiteY(int _x, int _y) {
		int x = _x;
		int y = _y + 1;
		if (y < height) {
			while ((img.pixels[x + y * img.width]) < whiteValue) {
				y++;
				if (y >= height)
					return height - 1;
			}
		}
		return y - 1;
	}

	// LUMA
	int getFirstNotLumaY(int x, int y) {
		if (y < height) {
			while (luma(img.pixels[x + y * img.width]) < lumaValue) {
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
			while (luma(img.pixels[x + y * img.width]) > lumaValue) {
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
