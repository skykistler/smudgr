package io.smudgr.extensions.image.alg.op;

import io.smudgr.engine.param.NumberParameter;
import io.smudgr.extensions.image.alg.PixelIndexList;
import io.smudgr.extensions.image.alg.math.univariate.LumaFunction;
import io.smudgr.extensions.image.alg.math.univariate.UnivariateFunction;
import io.smudgr.util.PixelFrame;

/**
 * Particle Push takes pixels and displaces them by a univariate scaled shift
 * amount.
 */
public class ParticlePush extends ParallelOperation {

	private NumberParameter scale = new NumberParameter("Scale", this, 1, -1, 1, 0.01);
	private NumberParameter shift = new NumberParameter("Shift", this, 50, 0, 1000, 1);

	private UnivariateFunction univariate = new LumaFunction();
	private PixelFrame buffer;
	private int shiftParam;
	private double scaleParam;

	@Override
	public String getTypeName() {
		return "Particle Push";
	}

	@Override
	protected void preParallel(PixelFrame img) {
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
	public void postParallel(PixelFrame img) {
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
		public void executeParallel(PixelFrame img, PixelIndexList coords) {
			coordsLen = coords.size();
			shiftPixels(coords, img);
		}

		private void shiftPixels(PixelIndexList coords, PixelFrame img) {
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
