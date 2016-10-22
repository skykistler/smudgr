package io.smudgr.project.smudge.alg.op;

import java.util.ArrayList;

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
	private ArrayList<TByteArrayList> byteLists = new ArrayList<TByteArrayList>();
	private byte replaceByte = 0x12;

	public void init() {
		target.setContinuous(true);
		byteLists.add(redByteList);
		byteLists.add(greenByteList);
		byteLists.add(blueByteList);
	}

	public void execute(Frame img) {
		byte targetByte = (byte) target.getIntValue();
		int subAmount = amount.getIntValue();

		redByteList.ensureCapacity(img.getWidth() * img.getHeight());
		greenByteList.ensureCapacity(img.getWidth() * img.getHeight());
		blueByteList.ensureCapacity(img.getWidth() * img.getHeight());

		for (PixelIndexList coords : getAlgorithm().getSelectedPixels())
			process(coords, targetByte, img, replaceByte, subAmount);
	}

	private void process(PixelIndexList coords, byte target, Frame img, byte sub, int subAmount) {
		redByteList.resetQuick();
		greenByteList.resetQuick();
		blueByteList.resetQuick();

		TByteArrayList currentByteList;

		for (int index = 0; index < coords.size(); index++) {

			int color = img.pixels[coords.get(index)];

			for (int k = 0; k < 3; k++) {

				currentByteList = byteLists.get(k);

				byte temp = (byte) ((color >> (8 * k)) & 0x000000FF);

				// If we hit our target byte, add our replacement byte
				if (temp == target) {
					for (int i = 0; i < subAmount; i++)
						currentByteList.add(sub);

					continue;
				}

				// Otherwise, add the vanilla byte and continue
				currentByteList.add(temp);
			}

		}

		// We are done adding bytes to the image byte array
		// Now we can move onto reassigning "databent" values

		for (int index = 0; index < coords.size(); index++) {
			int coord = coords.get(index);

			byte b1 = redByteList.getQuick(index);
			byte b2 = greenByteList.getQuick(index);
			byte b3 = blueByteList.getQuick(index);

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