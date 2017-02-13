package io.smudgr.engine.alg.op;

import gnu.trove.list.array.TByteArrayList;
import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.alg.math.ColorHelper;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.util.Frame;

/**
 * Data Bend mimics the popular Wordpad effect, with a configurable 'Target'
 * byte to replace a set 'Amount' of times.
 */
public class DataBend extends ParallelOperation {

	@Override
	public String getName() {
		return "Data Bend";
	}

	private NumberParameter target = new NumberParameter("Target", this, 125, 0, 255, 1);
	private NumberParameter amount = new NumberParameter("Amount", this, 1, 1, 100, 1);

	private byte replaceByte = 0x12;

	private byte targetByte;
	private int subAmount;

	@Override
	public void onInit() {
		target.setContinuous(true);
	}

	@Override
	public void preParallel(Frame img) {
		targetByte = (byte) target.getIntValue();
		subAmount = amount.getIntValue();
	}

	@Override
	public ParallelOperationTask getParallelTask() {
		return new DataBendTask();
	}

	class DataBendTask extends ParallelOperationTask {

		private TByteArrayList byteList = new TByteArrayList();
		private int index, color, k, i, coord, baseIndex, val, r, g, b;
		private byte temp;

		@Override
		public void executeParallel(Frame img, PixelIndexList coords) {
			byteList.ensureCapacity(coords.size() * 3);

			process(coords, targetByte, img, replaceByte, subAmount);
		}

		private void process(PixelIndexList coords, byte target, Frame img, byte sub, int subAmount) {
			byteList.resetQuick();

			for (index = 0; index < coords.size(); index++) {

				color = img.pixels[coords.get(index)];

				for (k = 0; k < 3; k++) {
					temp = (byte) ((color >> (8 * k)) & 0x000000FF);

					// If we hit our target byte, add our replacement byte
					if (temp == target) {
						for (i = 0; i < subAmount; i++)
							byteList.add(sub);

						continue;
					}

					// Otherwise, add the vanilla byte and continue
					byteList.add(temp);
				}

			}

			// We are done adding bytes to the image byte array
			// Now we can move onto reassigning "databent" values

			for (index = 0; index < coords.size(); index++) {
				coord = coords.get(index);
				baseIndex = index * 3;

				r = Byte.toUnsignedInt(byteList.getQuick(baseIndex + 2));
				g = Byte.toUnsignedInt(byteList.getQuick(baseIndex + 1));
				b = Byte.toUnsignedInt(byteList.getQuick(baseIndex));

				val = ColorHelper.color(255, r, g, b);

				img.pixels[coord] = val;
			}

		}
	}

}
