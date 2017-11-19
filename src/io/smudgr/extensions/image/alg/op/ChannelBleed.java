package io.smudgr.extensions.image.alg.op;

import io.smudgr.engine.param.NumberParameter;
import io.smudgr.extensions.image.alg.PixelIndexList;
import io.smudgr.util.PixelFrame;

/**
 * Channel Bleed rotates colors using a bitwise function for a weird digi-acid
 * effect.
 */
public class ChannelBleed extends ParallelOperation {

	@Override
	public String getTypeName() {
		return "Channel Bleed";
	}

	private NumberParameter shift = new NumberParameter("Shift Amount", this, 0, 0, 23, 1);

	private int rotateAmount;

	@Override
	public void preParallel(PixelFrame img) {
		rotateAmount = shift.getIntValue();
	}

	@Override
	public ParallelOperationTask getParallelTask() {
		return new ChannelBleedTask();
	}

	class ChannelBleedTask extends ParallelOperationTask {

		private int i, index, pixel, k;

		@Override
		public void executeParallel(PixelFrame img, PixelIndexList coords) {
			rotatePixels(img, coords);
		}

		private void rotatePixels(PixelFrame img, PixelIndexList coords) {
			for (i = 0; i < coords.size(); i++) {
				index = coords.get(i);
				pixel = img.pixels[index] & 0x00ffffff;
				k = rotateAmount;

				pixel = (pixel >>> k) | (pixel << (24 - k));

				img.pixels[index] = pixel | 0xff000000;
			}
		}
	}
}
