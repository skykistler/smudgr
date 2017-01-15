package io.smudgr.smudge.alg.op;

import gnu.trove.list.array.TByteArrayList;
import io.smudgr.smudge.alg.PixelIndexList;
import io.smudgr.smudge.alg.math.ColorHelper;
import io.smudgr.smudge.param.NumberParameter;
import io.smudgr.util.Frame;

public class DataBend extends Operation {

	public String getName() {
		return "Data Bend";
	}

	private NumberParameter target = new NumberParameter("Target", this, 125, 0, 255, 1);
	private NumberParameter amount = new NumberParameter("Amount", this, 1, 1, 100, 1);

	private TByteArrayList byteList = new TByteArrayList();
	private byte replaceByte = 0x12;

	public void init() {
		target.setContinuous(true);
	}

	public void execute(Frame img) {
		byte targetByte = (byte) target.getIntValue();
		int subAmount = amount.getIntValue();

		byteList.ensureCapacity(img.getWidth() * img.getHeight() * 3);

		for (PixelIndexList coords : getAlgorithm().getSelectedPixels())
			process(coords, targetByte, img, replaceByte, subAmount);
	}

	private void process(PixelIndexList coords, byte target, Frame img, byte sub, int subAmount) {
		byteList.resetQuick();

		for (int index = 0; index < coords.size(); index++) {

			int color = img.pixels[coords.get(index)];

			for (int k = 0; k < 3; k++) {
				byte temp = (byte) ((color >> (8 * k)) & 0x000000FF);

				// If we hit our target byte, add our replacement byte
				if (temp == target) {
					for (int i = 0; i < subAmount; i++)
						byteList.add(sub);

					continue;
				}

				// Otherwise, add the vanilla byte and continue
				byteList.add(temp);
			}

		}

		// We are done adding bytes to the image byte array
		// Now we can move onto reassigning "databent" values

		for (int index = 0; index < coords.size(); index++) {
			int coord = coords.get(index);
			int baseIndex = index * 3;

			byte b1 = byteList.getQuick(baseIndex);
			byte b2 = byteList.getQuick(baseIndex + 1);
			byte b3 = byteList.getQuick(baseIndex + 2);

			int val = createPixelValue(b1, b2, b3);
			img.pixels[coord] = val;
		}

	}

	private int createPixelValue(byte b1, byte b2, byte b3) {

		int r = Byte.toUnsignedInt(b3);
		int g = Byte.toUnsignedInt(b2);
		int b = Byte.toUnsignedInt(b1);

		return ColorHelper.color(255, r, g, b);
	}

}
