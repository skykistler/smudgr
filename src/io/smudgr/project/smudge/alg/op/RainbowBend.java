package io.smudgr.project.smudge.alg.op;

import gnu.trove.list.array.TByteArrayList;
import io.smudgr.project.smudge.alg.PixelIndexList;
import io.smudgr.project.smudge.alg.math.ColorHelper;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.project.util.Frame;

public class RainbowBend extends Operation {

	public String getName() {
		return "Rainbow Bend";
	}

	private NumberParameter target = new NumberParameter("Target", this, 125, 0, 255, 1);
	private NumberParameter amount = new NumberParameter("Amount", this, 1, 1, 100, 1);

	private TByteArrayList redByteList = new TByteArrayList();
	private TByteArrayList greenByteList = new TByteArrayList();
	private TByteArrayList blueByteList = new TByteArrayList();
	private byte replaceByte = 0x12;

	byte[] sub;
	int lastSubAmount;

	public void init() {
		target.setContinuous(true);
		int subAmount = amount.getIntValue();
		lastSubAmount = subAmount;
		sub = new byte[subAmount];
	}

	public void execute(Frame img) {
		byte targetByte = (byte) target.getIntValue();
		int subAmount = amount.getIntValue();

		if (subAmount != lastSubAmount) {
			sub = new byte[subAmount];
			byteFill(sub, replaceByte);
		}

		lastSubAmount = subAmount;

		int imgSize = img.getWidth() * img.getHeight();

		redByteList.ensureCapacity(imgSize);
		greenByteList.ensureCapacity(imgSize);
		blueByteList.ensureCapacity(imgSize);

		for (PixelIndexList coords : getAlgorithm().getSelectedPixels())
			process(coords, targetByte, img);
	}

	private void process(PixelIndexList coords, byte target, Frame img) {
		redByteList.resetQuick();
		greenByteList.resetQuick();
		blueByteList.resetQuick();

		for (int index = 0; index < coords.size(); index++) {

			int color = img.pixels[coords.get(index)];

			// If we hit our target byte, add our replacement byte
			// Otherwise, add the vanilla byte

			// First with red:
			byte temp = (byte) (color & 0x000000ff);
			if (temp == target)
				redByteList.add(sub);
			else
				redByteList.add(temp);

			// Second with green:
			temp = (byte) ((color >> 8) & 0x000000ff);
			if (temp == target)
				greenByteList.add(sub);
			else
				greenByteList.add(temp);

			// Third with blue:
			temp = (byte) ((color >> 16) & 0x000000ff);
			if (temp == target)
				blueByteList.add(sub);
			else
				blueByteList.add(temp);
		}

		// We are done adding bytes to the image byte array
		// Now we can move onto reassigning "databent" channel values

		for (int index = 0; index < coords.size(); index++) {
			int coord = coords.get(index);

			byte b1 = redByteList.getQuick(index);
			byte b2 = greenByteList.getQuick(index);
			byte b3 = blueByteList.getQuick(index);

			int val = createPixelValue(b1, b2, b3);
			img.pixels[coord] = val;
		}

	}

	private void byteFill(byte[] array, byte value) {
		int len = array.length;

		if (len > 0) {
			array[0] = value;
		}

		for (int i = 1; i < len; i += i) {
			System.arraycopy(array, 0, array, i, ((len - i) < i) ? (len - i) : i);
		}
	}

	private int createPixelValue(byte b1, byte b2, byte b3) {

		int r = 0x000000ff & (0xff & b1);
		int g = 0x000000ff & (0xff & b2);
		int b = 0x000000ff & (0xff & b3);

		return ColorHelper.color(0xff, r, g, b);
	}

}