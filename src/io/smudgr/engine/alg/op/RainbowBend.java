package io.smudgr.engine.alg.op;

import gnu.trove.list.array.TByteArrayList;
import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.alg.math.ColorHelper;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.util.Frame;

/**
 * I actually have no idea what this does. :)
 */
public class RainbowBend extends ParallelOperation {

	@Override
	public String getElementName() {
		return "Rainbow Bend";
	}

	private NumberParameter target = new NumberParameter("Target", this, 125, 0, 255, 1);
	private NumberParameter amount = new NumberParameter("Amount", this, 1, 1, 1000, 1);

	private byte replaceByte = 0x12;

	private byte[] sub;
	private byte targetByte;
	private int subAmount, lastSubAmount, len, i;

	@Override
	public void onInit() {
		target.setContinuous(true);
		subAmount = amount.getIntValue();
		lastSubAmount = subAmount;

		sub = new byte[subAmount];
		byteFill(sub, replaceByte);
	}

	@Override
	public void preParallel(Frame img) {
		targetByte = (byte) target.getIntValue();
		subAmount = amount.getIntValue();

		if (subAmount != lastSubAmount) {
			sub = new byte[subAmount];
			byteFill(sub, replaceByte);
		}

		lastSubAmount = subAmount;
	}

	private void byteFill(byte[] array, byte value) {
		len = array.length;

		if (len > 0) {
			array[0] = value;
		}

		for (i = 1; i < len; i += i) {
			System.arraycopy(array, 0, array, i, ((len - i) < i) ? (len - i) : i);
		}
	}

	@Override
	public ParallelOperationTask getParallelTask() {
		return new RainbowBendTask();
	}

	class RainbowBendTask extends ParallelOperationTask {

		private TByteArrayList redByteList = new TByteArrayList();
		private TByteArrayList greenByteList = new TByteArrayList();
		private TByteArrayList blueByteList = new TByteArrayList();

		private byte temp;
		private int index, color, r, g, b;

		@Override
		public void executeParallel(Frame img, PixelIndexList coords) {
			redByteList.ensureCapacity(coords.size());
			greenByteList.ensureCapacity(coords.size());
			blueByteList.ensureCapacity(coords.size());

			process(coords, targetByte, img);
		}

		private void process(PixelIndexList coords, byte target, Frame img) {
			redByteList.resetQuick();
			greenByteList.resetQuick();
			blueByteList.resetQuick();

			for (index = 0; index < coords.size(); index++) {

				color = img.pixels[coords.getQuick(index)];

				// If we hit our target byte, add our replacement byte
				// Otherwise, add the vanilla byte

				// First with red:
				temp = (byte) (color >> 16);
				if (temp == target)
					redByteList.add(sub);
				else
					redByteList.add(temp);

				// Second with green:
				temp = (byte) (color >> 8);
				if (temp == target)
					greenByteList.add(sub);
				else
					greenByteList.add(temp);

				// Third with blue:
				temp = (byte) (color);
				if (temp == target)
					blueByteList.add(sub);
				else
					blueByteList.add(temp);
			}

			// We are done adding bytes to the image byte array
			// Now we can move onto reassigning "databent" channel values

			for (index = 0; index < coords.size(); index++) {
				r = Byte.toUnsignedInt(redByteList.getQuick(index));
				g = Byte.toUnsignedInt(greenByteList.getQuick(index));
				b = Byte.toUnsignedInt(blueByteList.getQuick(index));

				img.pixels[coords.getQuick(index)] = ColorHelper.color(0xff, r, g, b);
			}

		}
	}

}