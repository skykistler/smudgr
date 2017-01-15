package io.smudgr.engine.alg.op;

import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.util.Frame;

public class ChannelBleed extends Operation {

	public String getName() {
		return "Channel Bleed";
	}

	private NumberParameter shift = new NumberParameter("Shift Amount", this, 0, 0, 23, 1);
	// private UnivariateParameter function = new
	// UnivariateParameter("Function", this, new LumaFunction());

	// private UnivariateFunction univariate = null;
	private int rotateAmount;

	public void execute(Frame img) {
		rotateAmount = shift.getIntValue();
		// univariate = function.getValue();

		for (PixelIndexList coords : getAlgorithm().getSelectedPixels())
			rotatePixels(coords, rotateAmount, img);
	}

	private void rotatePixels(PixelIndexList coords, int shift, Frame img) {
		for (int i = 0; i < coords.size(); i++) {
			int index = coords.get(i);
			int pixel = img.pixels[index] & 0x00ffffff;
			int k = shift; // (int) ((univariate.calculate(pixel)) * shift);

			pixel = (pixel >>> k) | (pixel << (24 - k));

			img.pixels[index] = pixel | 0xff000000;
		}
	}

}
