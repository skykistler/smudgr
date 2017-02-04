package io.smudgr.engine.alg.op;

import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.alg.math.univariate.LumaFunction;
import io.smudgr.engine.alg.math.univariate.UnivariateFunction;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.util.Frame;

/**
 * Particle Push takes pixels and displaces them by a univariate scaled shift
 * amount.
 */
public class ParticlePush extends Operation {

	private NumberParameter scale = new NumberParameter("Scale", this, 1, -1, 1, 0.01);
	private NumberParameter shift = new NumberParameter("Shift", this, 50, 0, 1000, 1);

	private UnivariateFunction univariate = new LumaFunction();
	private int currentShift;
	private double currentScale;

	@Override
	public String getName() {
		return "Particle Push";
	}

	@Override
	public void execute(Frame img) {

		currentShift = shift.getIntValue();
		currentScale = scale.getValue();

		Frame buffer = new Frame(img.getWidth(), img.getHeight());

		for (PixelIndexList coords : getAlgorithm().getSelectedPixels()) {
			shiftPixels(coords, img, buffer);
		}

		buffer.copyTo(img);
		buffer.dispose();
	}

	private void shiftPixels(PixelIndexList coords, Frame img, Frame bufferImg) {
		int coordsLen = coords.size();
		for (int i = 0; i < coordsLen; i++) {
			int index = coords.get(i);
			int pixel = img.pixels[index];
			double scale = univariate.calculate(pixel) * currentScale;
			int offset = (int) (currentShift * scale);
			int newIndex = coords.get(Math.floorMod(i + offset, coordsLen));
			bufferImg.pixels[newIndex] = pixel;
		}
	}

}
