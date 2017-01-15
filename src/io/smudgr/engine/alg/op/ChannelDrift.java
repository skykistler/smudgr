package io.smudgr.engine.alg.op;

import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.alg.math.ColorHelper;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.util.Frame;

public class ChannelDrift extends Operation {

	public String getName() {
		return "Channel Drift";
	}

	NumberParameter redX = new NumberParameter("Red Offset - X", this, 0, 0, 1, 0.001);
	NumberParameter redY = new NumberParameter("Red Offset - Y", this, 0, 0, 1, 0.001);
	NumberParameter greenX = new NumberParameter("Green Offset - X", this, 0, 0, 1, 0.001);
	NumberParameter greenY = new NumberParameter("Green Offset - Y", this, 0, 0, 1, 0.001);
	NumberParameter blueX = new NumberParameter("Blue Offset - X", this, 0, 0, 1, 0.001);
	NumberParameter blueY = new NumberParameter("Blue Offset - Y", this, 0, 0, 1, 0.001);

	private int boundWidth;
	private int boundHeight;

	public void init() {
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
		boundWidth = getAlgorithm().getBound().getTranslatedWidth(img);
		boundHeight = getAlgorithm().getBound().getTranslatedHeight(img);

		Frame copy = img.copy();

		int redShiftX = (int) (redX.getValue() * boundWidth);
		int redShiftY = (int) (redY.getValue() * boundHeight);
		int greenShiftX = (int) (greenX.getValue() * boundWidth);
		int greenShiftY = (int) (greenY.getValue() * boundHeight);
		int blueShiftX = (int) (blueX.getValue() * boundWidth);
		int blueShiftY = (int) (blueY.getValue() * boundHeight);

		for (PixelIndexList coords : getAlgorithm().getSelectedPixels()) {
			for (int index = 0; index < coords.size(); index++) {
				int coord = coords.get(index);

				int x = coord % copy.getWidth();
				int y = (coord - x) / copy.getWidth();

				int r = (int) ColorHelper.red(getShifted(img, x, y, redShiftX, redShiftY));
				int g = (int) ColorHelper.green(getShifted(img, x, y, greenShiftX, greenShiftY));
				int b = (int) ColorHelper.blue(getShifted(img, x, y, blueShiftX, blueShiftY));

				copy.pixels[coord] = ColorHelper.color(255, r, g, b);
			}
		}

		copy.copyTo(img);
		copy.dispose();
	}

	public int getShifted(Frame orig, int x, int y, int shiftX, int shiftY) {
		int x1 = (x + shiftX) % boundWidth;
		int y1 = (y + shiftY) % boundHeight;

		return orig.get(x1, y1);
	}

}