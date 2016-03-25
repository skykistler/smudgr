package io.smudgr.source.smudge.alg.op;

import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.PixelIndexList;
import io.smudgr.source.smudge.alg.math.ColorHelper;
import io.smudgr.source.smudge.param.NumberParameter;
import gnu.trove.list.array.TByteArrayList;

public class ByteReplace extends Operation {
	
	NumberParameter precursor = new NumberParameter("Precursor", this, 0, 0, 255, 1);
	
	NumberParameter sub1 = new NumberParameter("Substitute 1", this, 0, 0, 255, 1);
	NumberParameter sub2 = new NumberParameter("Substitute 2", this, 0, 0, 255, 1);
	
	public String getName() {
		return "Byte Replace";
	}
	
	public void execute(Frame img) {
		byte s1 = (byte) sub1.getIntValue();
		byte s2 = (byte) sub2.getIntValue();
		
		byte[] sub = new byte[] {s1, s2};
		
		byte orig = (byte) precursor.getIntValue();
		
		for (PixelIndexList coords : getAlgorithm().getSelectedPixels()) {
			process(coords, orig, img, sub);
		}
	}

	private void process(PixelIndexList coords, byte orig, Frame img, byte[] sub) {
		
		TByteArrayList image = new TByteArrayList();
		
		for(int index = 0; index < coords.size(); index++) {
	
			int color = img.pixels[coords.get(index)];
			
			for(int k = 0; k < 4; k++) {
				byte temp = (byte)((color >> (8 * k)) & 0xFF);
				if (temp == orig) {
					for(byte s : sub) {
						image.add(s);
					}
				}
				else {
					image.add(temp);
				}
			}
			
		}
		
		// We are done adding bytes to the image byte array
		// Now we can move onto reassigning "databent" values
		
		byte[] imageBytes = image.toArray();
		
		for(int index = 0; index < coords.size(); index++) {
			int coord = coords.get(index);
			int baseIndex = index * 4;
			
			byte b1 = imageBytes[baseIndex];
			byte b2 = imageBytes[baseIndex + 1];
			byte b3 = imageBytes[baseIndex + 2];
			byte b4 = imageBytes[baseIndex + 3];
			
			int val = createPixelValue(b1, b2, b3, b4);
			img.pixels[coord] = val;
		}	
		
	}
	
	private int createPixelValue(byte b1, byte b2, byte b3, byte b4) {
		
		int a = (int) b1;
		int r = (int) b2;
		int g = (int) b3;
		int b = (int) b4;
		
		return ColorHelper.color(a, r, g, b);
	}

}
