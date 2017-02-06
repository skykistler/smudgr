package io.smudgr.engine.alg.bound;

import io.smudgr.engine.alg.Algorithm;
import io.smudgr.engine.alg.AlgorithmComponent;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.util.Frame;

/**
 * The {@link Bound} class is an {@link AlgorithmComponent} that defines
 * behavior for restricting the focus of an {@link Algorithm} to a parameterized
 * rectangle.
 * <p>
 * {@link Bound} instances run before any other {@link AlgorithmComponent} in
 * the chain, so setting a small bound area may improve {@link Algorithm}
 * performance on high resolution images.
 */
public class Bound extends AlgorithmComponent {

	@Override
	public String getComponentName() {
		return "Bound";
	}

	@Override
	public String getComponentIdentifier() {
		return "bound";
	}

	@Override
	public String getName() {
		return "Rectangle";
	}

	@Override
	public String getIdentifier() {
		return "rectangle";
	}

	protected NumberParameter offsetX = new NumberParameter("Bound X", this, 0, 0, 1, 0.005);
	protected NumberParameter offsetY = new NumberParameter("Bound Y", this, 0, 0, 1, 0.005);
	protected NumberParameter width = new NumberParameter("Bound Width", this, 1, 0, 1, 0.005);
	protected NumberParameter height = new NumberParameter("Bound Height", this, 1, 0, 1, 0.005);

	@Override
	public void init() {

	}

	@Override
	public void update() {

	}

	/**
	 * Checks whether a given point is contained within the current state of
	 * this {@link Bound}, given {@link Frame} dimensions
	 *
	 * @param x
	 *            coordinate to test
	 * @param y
	 *            coordinate to test
	 * @param w
	 *            width of {@link Frame}
	 * @param h
	 *            height of {@link Frame}
	 * @return {@code true} if this {@link Bound} contains the given point,
	 *         {@code false} if otherwise
	 */
	public boolean containsPoint(int x, int y, int w, int h) {
		double offsetX = this.offsetX.getValue();
		double offsetY = this.offsetY.getValue();
		double width = this.width.getValue();
		double height = this.height.getValue();

		boolean inImage = x >= 0 && y >= 0 && x < w && y < h;
		if (!inImage)
			return false;

		if (offsetX == 0 && offsetY == 0 && width == 1 && height == 1)
			return true;

		// Transform x and y coordinates to percentages
		double bX = (double) x / w;
		double bY = (double) y / h;

		if (bX < offsetX + width && x >= offsetX)
			if (bY < offsetY + height && y >= offsetY)
				return true;

		return false;
	}

	/**
	 * Gets the x-position of the rectangle as a percentage of any given image's
	 * width, between 0-1
	 *
	 * @return Starting x-position of rectangle, between 0-1
	 * @see Bound#getWidth()
	 */
	public double getOffsetX() {
		return offsetX.getValue();
	}

	/**
	 * Sets the x-position of the rectangle as a percentage of any given image's
	 * width,
	 * between 0-1
	 *
	 * @param offsetX
	 *            Starting x-position of rectangle, between 0-1
	 * @see Bound#setWidth(double)
	 */
	public void setOffsetX(double offsetX) {
		this.offsetX.setValue(offsetX);
	}

	/**
	 * Gets the y-position of the rectangle as a percentage of any given image's
	 * height,
	 * between 0-1
	 *
	 * @return Starting y-position of rectangle, between 0-1
	 * @see Bound#getHeight()
	 */
	public double getOffsetY() {
		return offsetY.getValue();
	}

	/**
	 * Sets the y-position of the rectangle as a percentage of any given image's
	 * height,
	 * between 0-1
	 *
	 * @param offsetY
	 *            Starting y-position of rectangle, between 0-1
	 * @see Bound#setHeight(double)
	 */
	public void setOffsetY(double offsetY) {
		this.offsetY.setValue(offsetY);
	}

	/**
	 * Gets the width of the rectangle as a percentage of any given image's
	 * width,
	 * between 0-1
	 *
	 * @return width of rectangle, between 0-1
	 * @see Bound#getOffsetX()
	 */
	public double getWidth() {
		return width.getValue();
	}

	/**
	 * Sets the width of the rectangle as a percentage of any given image's
	 * width,
	 * between 0-1
	 *
	 * @param width
	 *            of rectangle, between 0-1
	 * @see Bound#setOffsetX(double)
	 */
	public void setWidth(double width) {
		this.width.setValue(width);
	}

	/**
	 * Gets the height of the rectangle as a percentage of any given image's
	 * height,
	 * between 0-1
	 *
	 * @return height of rectangle, between 0-1
	 * @see Bound#getOffsetY()
	 */
	public double getHeight() {
		return height.getValue();
	}

	/**
	 * Sets the height of the rectangle as a percentage of any given image's
	 * height,
	 * between 0-1
	 *
	 * @param height
	 *            of rectangle, between 0-1
	 * @see Bound#setOffsetY(double)
	 */
	public void setHeight(double height) {
		this.height.setValue(height);
	}

	/**
	 * Get the actual pixel starting x-coordinate of this rectangle for a given
	 * image width
	 *
	 * @param imageWidth
	 *            {@code int}
	 * @return Actual pixel position of the starting x-coordinate of the
	 *         rectangle
	 */
	public int getTranslatedX(int imageWidth) {
		return (int) Math.floor(offsetX.getValue() * imageWidth);
	}

	/**
	 * Get the actual pixel starting y-coordinate of this rectangle for a given
	 * image height
	 *
	 * @param imageHeight
	 *            {@code int}
	 * @return Actual pixel position of the starting y-coordinate of the
	 *         rectangle
	 */
	public int getTranslatedY(int imageHeight) {
		return (int) Math.floor(offsetY.getValue() * imageHeight);
	}

	/**
	 * Get the actual pixel width of this rectangle for a given image width
	 *
	 * @param imageWidth
	 *            {@code int}
	 * @return Actual pixel width of this rectangle
	 */
	public int getTranslatedWidth(int imageWidth) {
		return (int) Math.floor(width.getValue() * imageWidth);
	}

	/**
	 * Get the actual pixel height of this rectangle for a given image height
	 *
	 * @param imageHeight
	 *            {@code int}
	 * @return Actual pixel height of this rectangle
	 */
	public int getTranslatedHeight(int imageHeight) {
		return (int) Math.floor(height.getValue() * imageHeight);
	}

}
