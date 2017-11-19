package io.smudgr.engine.utility.crop;

import io.smudgr.engine.param.NumberParameter;
import io.smudgr.engine.utility.UtilityComponent;
import io.smudgr.util.PixelFrame;

/**
 * The {@link Cropper} class defines a {@link UtilityComponent} that allows
 * parameterized cropping on the {@link PixelFrame} passed in (Basically has a
 * Parameter and calls Frame.resize).
 */
public class Cropper extends UtilityComponent {

	@Override
	public String getTypeName() {
		return "Cropper";
	}

	@Override
	public String getComponentTypeName() {
		return "Cropper";
	}

	@Override
	public String getComponentTypeIdentifier() {
		return "cropper";
	}

	protected NumberParameter offsetX = new NumberParameter("X Offset", this, 0, 0, 1, 0.005);
	protected NumberParameter offsetY = new NumberParameter("Y Offset", this, 0, 0, 1, 0.005);
	protected NumberParameter width = new NumberParameter("X Width", this, 1, 0.005, 1, 0.005);
	protected NumberParameter height = new NumberParameter("Y Height", this, 1, 0.005, 1, 0.005);

	private int frameWidth, frameHeight, xOffset, yOffset, w, h;

	/**
	 * Crops the given frame.
	 * 
	 * @param f
	 *            {@link PixelFrame}
	 * @return new {@link PixelFrame}
	 */
	public PixelFrame crop(PixelFrame f) {

		frameWidth = f.getWidth();
		frameHeight = f.getHeight();

		xOffset = (int) (offsetX.getValue() * frameWidth);
		yOffset = (int) (offsetY.getValue() * frameHeight);
		w = (int) (width.getValue() * frameWidth);
		h = (int) (height.getValue() * frameHeight);

		if (xOffset == 0 && yOffset == 0 && w == frameWidth && h == frameHeight)
			return f;

		w = Math.min(frameWidth, w + xOffset);
		h = Math.min(frameHeight, h + yOffset);

		PixelFrame croppedFrame = f.resize(xOffset, yOffset, w, h, w, h);
		f.dispose();

		return croppedFrame;
	}

	/**
	 * @return height's double value
	 */
	public double getHeight() {
		return height.getValue();
	}

	/**
	 * @return width's double value
	 */
	public double getWidth() {
		return width.getValue();
	}

	/**
	 * @return offsetY's double value
	 */
	public double getOffsetY() {
		return offsetY.getValue();
	}

	/**
	 * @return offsetX's double value
	 */
	public double getOffsetX() {
		return offsetX.getValue();
	}

}
