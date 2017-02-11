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
	private int shiftParam, coordsLen, i, index, pixel, offset, newIndex;
	private double scaleParam, currentScale;

	@Override
	public String getName() {
		return "Particle Push";
	}

	@Override
	public void execute(Frame img) {
		shiftParam = shift.getIntValue();
		scaleParam = scale.getValue();

		Frame buffer = new Frame(img.getWidth(), img.getHeight());

		for (PixelIndexList coords : getAlgorithm().getSelectedPixels()) {
			shiftPixels(coords, img, buffer);
		}

		buffer.copyTo(img);
		buffer.dispose();
	}

	private void shiftPixels(PixelIndexList coords, Frame img, Frame bufferImg) {
		coordsLen = coords.size();
		for (i = 0; i < coordsLen; i++) {
			index = coords.get(i);
			pixel = img.pixels[index];
			currentScale = univariate.calculate(pixel) * scaleParam;
			offset = (int) (shiftParam * currentScale);
			newIndex = coords.get(Math.floorMod(i + offset, coordsLen));
			bufferImg.pixels[newIndex] = pixel;
		}
	}

}
