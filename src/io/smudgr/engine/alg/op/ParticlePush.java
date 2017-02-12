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
public class ParticlePush extends ParallelOperation {

	private NumberParameter scale = new NumberParameter("Scale", this, 1, -1, 1, 0.01);
	private NumberParameter shift = new NumberParameter("Shift", this, 50, 0, 1000, 1);

	private UnivariateFunction univariate = new LumaFunction();
	private Frame buffer;
	private int shiftParam;
	private double scaleParam;

	@Override
	public String getName() {
		return "Particle Push";
	}

	@Override
	protected void preParallel(Frame img) {
		shiftParam = shift.getIntValue();
		scaleParam = scale.getValue();

		if (buffer == null || buffer.getWidth() != img.getWidth() || buffer.getHeight() != img.getHeight()) {
			if (buffer != null)
				buffer.dispose();

			buffer = img.copy();
		} else {
			img.copyTo(buffer);
		}
	}

	@Override
	public void postParallel(Frame img) {
		buffer.copyTo(img);
	}

	@Override
	public ParallelOperationTask getParallelTask() {
		return new ParticlePushTask();
	}

	class ParticlePushTask extends ParallelOperationTask {

		private int coordsLen, i, index, pixel, offset, newIndex;
		private double currentScale;

		@Override
		public void executeParallel(Frame img, PixelIndexList coords) {
			coordsLen = coords.size();
			shiftPixels(coords, img);
		}

		private void shiftPixels(PixelIndexList coords, Frame img) {
			for (i = 0; i < coordsLen; i++) {
				index = coords.get(i);
				pixel = img.pixels[index];
				currentScale = univariate.calculate(pixel) * scaleParam;
				offset = (int) (shiftParam * currentScale);
				newIndex = coords.get(Math.floorMod(i + offset, coordsLen));
				buffer.pixels[newIndex] = pixel;
			}
		}

	}

}
