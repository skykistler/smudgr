package io.smudgr.engine.alg.op;

import java.util.Arrays;

import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.param.BooleanParameter;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.util.Frame;

/**
 * ChannelSort sorts pixels but on a masked portion of the pixel.
 */
public class ChannelSort extends ParallelOperation {

	@Override
	public String getTypeName() {
		return "Channel Sort";
	}

	private BooleanParameter reverse = new BooleanParameter("Reverse", this, false);
	private NumberParameter channel = new NumberParameter("Channel Step", this, 0, 0, 24, 1);

	private NumberParameter sortQuality = new NumberParameter("Sort Quality", this, 7, 0, 7, 1);

	// Declared for memory reuse
	private int quality, mask, invertMask;
	private boolean reverseVal;

	@Override
	public void onInit() {
	}

	@Override
	public void preParallel(Frame img) {
		reverseVal = reverse.getValue();

		quality = 7 - sortQuality.getIntValue();

		mask = (0x000000ff >>> quality) << quality;

		mask = mask << channel.getIntValue();
		invertMask = ~mask;
	}

	@Override
	public ParallelOperationTask getParallelTask() {
		return new ChannelSortTask();
	}

	class ChannelSortTask extends ParallelOperationTask {

		private int[] toSort = new int[1024];
		private int sortSize, i, pixel, index;

		@Override
		public void executeParallel(Frame img, PixelIndexList coords) {
			sortSize = coords.size();

			if (toSort.length < sortSize)
				toSort = new int[sortSize];

			sortList(img, coords, mask);
		}

		private void sortList(Frame img, PixelIndexList coords, int mask) {

			for (i = 0; i < sortSize; i++)
				toSort[i] = img.pixels[coords.get(i)] & mask;

			Arrays.sort(toSort);

			for (i = 0; i < sortSize; i++) {
				pixel = (img.pixels[coords.get(i)] & invertMask) | toSort[i];
				index = reverseVal ? ((sortSize - 1) - i) : i;
				img.pixels[coords.get(index)] = pixel;
			}
		}
	}

}
