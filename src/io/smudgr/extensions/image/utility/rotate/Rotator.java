package io.smudgr.extensions.image.utility.rotate;

import io.smudgr.engine.param.NumberParameter;
import io.smudgr.extensions.image.utility.UtilityComponent;
import io.smudgr.util.PixelFrame;

/**
 * The {@link Rotator} class defines a {@link UtilityComponent} that allows
 * Frames to be rotated.
 */
public class Rotator extends UtilityComponent {

	@Override
	public String getTypeName() {
		return "Rotator";
	}

	@Override
	public String getComponentTypeName() {
		return "Rotator";
	}

	@Override
	public String getComponentTypeIdentifier() {
		return "rotator";
	}

	private PixelFrame lastRotatedImage = null;
	private NumberParameter rotationDegree = new NumberParameter("Rotation Degree", this, 0, 0, 270, 90);
	private int lastWidth, lastHeight, toWidth, toHeight, frameWidth, frameHeight;
	private int currentRotationDegree, toY, toX, x, y, temp;

	/**
	 * Rotates the image clockwise by multiples of 90 degrees, limited from 0 to
	 * 270 degrees.
	 * 
	 * @param f
	 *            {@link PixelFrame}
	 * @return {@link PixelFrame}
	 */
	public PixelFrame rotate(PixelFrame f) {
		currentRotationDegree = rotationDegree.getIntValue();

		if (currentRotationDegree == 0)
			return f;

		frameWidth = f.getWidth();
		frameHeight = f.getHeight();

		toWidth = frameWidth;
		toHeight = frameHeight;

		// The following cluster of conditionals ensures that when
		// rotations are being done quickly, with a constant-sized Frame (frame
		// dimensions stick to w x h and h x w), they should put a minimal
		// amount of strain on Java's heap and the Frame allocator. This is
		// assuming that I can even use past frames again without worrying about
		// them being disposed.
		if (currentRotationDegree == 90 || currentRotationDegree == 270) {
			toWidth = frameHeight;
			toHeight = frameWidth;

			if (lastRotatedImage == null) {
				lastRotatedImage = new PixelFrame(toWidth, toHeight);
			}

			if (lastRotatedImage != null || lastHeight != toHeight || lastWidth != toWidth) {
				lastRotatedImage.dispose();
				lastRotatedImage = new PixelFrame(toWidth, toHeight);
			}

			// Use the lastRotatedImage frame to return the argument image
			// rotated 90 or 270 degrees.

			if (currentRotationDegree == 90) {
				for (x = 0; x < frameWidth; x++)
					for (y = 0; y < frameHeight; y++)
						lastRotatedImage.set(frameHeight - y - 1, x, f.get(x, y));
			} else if (currentRotationDegree == 270) {
				for (x = 0; x < frameWidth; x++)
					for (y = 0; y < frameHeight; y++)
						lastRotatedImage.set(y, frameWidth - x - 1, f.get(x, y));
			}

			lastHeight = toHeight;
			lastWidth = toWidth;

			f.dispose();
			return lastRotatedImage;

		} else /* 180 */ {
			// Use the passed in Frame f to rotate the image. Caching doesn't
			// need to be used for a rotation degree that keeps constant
			// dimensions for w x h. It can just be done in place as a cheap
			// swapping procedure.

			// In-place reverse raster lines followed by swap over mid-line of
			// image from top to bottom results in a 180 degree rotation.

			for (y = 0; y < toHeight; y++) {
				for (x = 0; x < toWidth / 2; x++) {
					toX = toWidth - x - 1;
					temp = f.get(x, y);
					f.set(x, y, f.get(toX, y));
					f.set(toX, y, temp);
				}
			}
			for (x = 0; x < toWidth; x++) {
				for (y = 0; y < toHeight / 2; y++) {
					toY = toHeight - y - 1;
					temp = f.get(x, y);
					f.set(x, y, f.get(x, toY));
					f.set(x, toY, temp);
				}
			}
			return f;
		}
	}

}
