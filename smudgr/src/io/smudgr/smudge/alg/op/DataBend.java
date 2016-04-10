package io.smudgr.smudge.alg.op;

import gnu.trove.list.array.TByteArrayList;
import io.smudgr.smudge.alg.PixelIndexList;
import io.smudgr.smudge.alg.math.ColorHelper;
import io.smudgr.smudge.param.NumberParameter;
import io.smudgr.smudge.source.Frame;

public class DataBend extends Operation {

	NumberParameter target = new NumberParameter("Target", this, 125, 0, 255, 1);

	NumberParameter amount = new NumberParameter("Amount", this, 1, 1, 100, 1);

	private byte replaceByte = 0x12;

	public String getName() {
		return "Data Bend";
	}

	public void init() {
		target.setContinuous(true);
	}

	public void execute(Frame img) {
		byte orig = (byte) target.getIntValue();
		int subAmount = amount.getIntValue();

		for (PixelIndexList coords : getAlgorithm().getSelectedPixels()) {
			process(coords, orig, img, replaceByte, subAmount);
		}
	}

	private void process(PixelIndexList coords, byte orig, Frame img, byte sub, int subAmount) {

		TByteArrayList image = new TByteArrayList();

		for (int index = 0; index < coords.size(); index++) {

			int color = img.pixels[coords.get(index)];

			for (int k = 0; k < 3; k++) {
				byte temp = (byte) ((color >> (8 * k)) & 0x000000FF);
				if (temp == orig) {
					for (int i = 0; i < subAmount; i++) {
						image.add(sub);
					}
				} else {
					image.add(temp);
				}
			}

		}

		// We are done adding bytes to the image byte array
		// Now we can move onto reassigning "databent" values

		byte[] imageBytes = image.toArray();

		for (int index = 0; index < coords.size(); index++) {
			int coord = coords.get(index);
			int baseIndex = index * 3;

			byte b1 = imageBytes[baseIndex];
			byte b2 = imageBytes[baseIndex + 1];
			byte b3 = imageBytes[baseIndex + 2];
			// byte b4 = imageBytes[baseIndex + 3];

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