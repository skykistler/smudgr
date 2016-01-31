package io.smudgr.alg;

import java.util.ArrayList;

import io.smudgr.Smudge;
import io.smudgr.alg.math.ColorHelper;
import io.smudgr.alg.param.NumberParameter;
import io.smudgr.source.Frame;

public class ChannelDrift extends Algorithm {

	NumberParameter redX = new NumberParameter(this, "Red Offset - X", 0, 0, 1);
	NumberParameter redY = new NumberParameter(this, "Red Offset - Y", 0, 0, 1);
	NumberParameter greenX = new NumberParameter(this, "Green Offset - X", 0, 0, 1);
	NumberParameter greenY = new NumberParameter(this, "Green Offset - Y", 0, 0, 1);
	NumberParameter blueX = new NumberParameter(this, "Blue Offset - X", 0, 0, 1);
	NumberParameter blueY = new NumberParameter(this, "Blue Offset - Y", 0, 0, 1);

	private int boundWidth;
	private int boundHeight;

	public ChannelDrift(Smudge s) {
		super(s);
		redX.setContinuous(true);
		redY.setContinuous(true);
		greenX.setContinuous(true);
		greenY.setContinuous(true);
		blueX.setContinuous(true);
		blueY.setContinuous(true);

		redX.setReverse(true);
		greenX.setReverse(true);
		blueX.setReverse(true);
	}

	public void execute(Frame img) {
		boundWidth = getBound().getTranslatedWidth(img);
		boundHeight = getBound().getTranslatedHeight(img);

		Frame copy = img.copy();

		int redShiftX = (int) (redX.getValue() * boundWidth);
		int redShiftY = (int) (redY.getValue() * boundHeight);
		int greenShiftX = (int) (greenX.getValue() * boundWidth);
		int greenShiftY = (int) (greenY.getValue() * boundHeight);
		int blueShiftX = (int) (blueX.getValue() * boundWidth);
		int blueShiftY = (int) (blueY.getValue() * boundHeight);

		for (ArrayList<Integer> coords : getCoordFunction().getCoordSet()) {
			for (Integer coord : coords) {
				int x = coord % copy.getWidth();
				int y = (coord - x) / copy.getWidth();

				int r = (int) ColorHelper.red(getShifted(x, y, redShiftX, redShiftY));
				int g = (int) ColorHelper.green(getShifted(x, y, greenShiftX, greenShiftY));
				int b = (int) ColorHelper.blue(getShifted(x, y, blueShiftX, blueShiftY));

				copy.pixels[coord] = ColorHelper.color(255, r, g, b);
			}
		}

		img.pixels = copy.pixels;
	}

	public int getShifted(int x, int y, int shiftX, int shiftY) {
		int x1 = (x + shiftX) % boundWidth;
		int y1 = (y + shiftY) % boundHeight;

		return img.get(x1, y1);
	}

	public String getName() {
		return "Channel Drift";
	}

}
