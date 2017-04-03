package io.smudgr.util;

import java.awt.image.BufferedImage;

/**
 * Instances of the {@link Frame} class are used to store the pixel data of an
 * image.
 * <p>
 * The
 * {@link DisposedFrameProvider} allows {@link Frame} instances to reuse memory
 * efficiently and avoid unnecessary string on the JVM garbage collector.
 */
public class Frame {
	private int width;
	private int height;
	private long disposedTime = 0;

	/**
	 * Direct access array of the ARGB color integers backing this
	 * {@link Frame}
	 */
	public int[] pixels;

	/**
	 * Create a new {@link Frame} with the given dimensions, without clearing
	 * the backing memory.
	 * <p>
	 * The new {@link Frame} may contain disposed pixel data. If this is a
	 * problem, use {@link Frame#Frame(BufferedImage)} instead.
	 *
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	public Frame(int w, int h) {
		width = w;
		height = h;

		pixels = getDirtyPixels();
	}

	/**
	 * Create a new {@link Frame} from the given {@link BufferedImage}
	 *
	 * @param image
	 *            {@link BufferedImage}
	 */
	public Frame(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();

		pixels = getDirtyPixels();

		image.getRGB(0, 0, width, height, pixels, 0, width);
	}

	/**
	 * Creates a duplicate of this {@link Frame}, using new memory space (or
	 * disposed memory if disposed frames of this size exist)
	 *
	 * @return duplicate {@link Frame}
	 */
	public synchronized Frame copy() {
		assertNotDisposed();

		Frame copy = new Frame(width, height);

		System.arraycopy(pixels, 0, copy.pixels, 0, pixels.length);

		return copy;
	}

	/**
	 * Copy this {@link Frame} to the given {@link Frame}, copying only until
	 * this {@link Frame} length is reached, or the given {@link Frame} length
	 * is reached.
	 *
	 * @param f
	 *            {@link Frame} of equal dimensions (ideally)
	 */
	public synchronized void copyTo(Frame f) {
		assertNotDisposed();

		System.arraycopy(pixels, 0, f.pixels, 0, Math.min(f.pixels.length, pixels.length));
	}

	/**
	 * Draw this frame to the given {@link BufferedImage}
	 * <p>
	 * This is especially useful for flushing pixels to screen if the given
	 * {@link BufferedImage} is backed by native screen space.
	 *
	 * @param image
	 *            {@link BufferedImage}
	 */
	public synchronized void drawTo(BufferedImage image) {
		assertNotDisposed();

		Frame fittedFrame = fitToSize(image.getWidth(), image.getHeight());

		try {
			image.setRGB(0, 0, fittedFrame.getWidth(), fittedFrame.getHeight(), fittedFrame.pixels, 0, fittedFrame.getWidth());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("orig width: " + width);
			System.out.println("orig height: " + height);
			System.out.println("to width: " + image.getWidth());
			System.out.println("to height: " + image.getHeight());
			System.out.println("fitted width: " + fittedFrame.getWidth());
			System.out.println("fitted height: " + fittedFrame.getHeight());
		}

		fittedFrame.dispose();
	}

	/**
	 * Without changing dimension ratios, fit this {@link Frame} to the given
	 * size and fill blank space with black.
	 *
	 * @param toSizeW
	 *            new width
	 * @param toSizeH
	 *            new height
	 * @return fitted {@link Frame}
	 */
	public Frame fitToSize(int toSizeW, int toSizeH) {
		return fitToSize(toSizeW, toSizeH, true);
	}

	/**
	 * Without changing dimension ratios, return a copy of this {@link Frame}
	 * fitted to the given size, optionally filling blank space with black.
	 *
	 * @param toSizeW
	 *            new width
	 * @param toSizeH
	 *            new height
	 * @param fill
	 *            If true, to return a frame of exactly toSizeW x toSizeH with
	 *            blank space filled with black. Otherwise, a frame with scaled
	 *            dimensions of the original ratio is returned.
	 * @return fitted {@link Frame}
	 */
	public Frame fitToSize(int toSizeW, int toSizeH, boolean fill) {
		assertNotDisposed();

		double newWidth = width;
		double newHeight = height;

		boolean tooSmall = newWidth < toSizeW && newHeight < toSizeH;

		if (width > toSizeW || tooSmall) {
			newWidth = toSizeW;
			newHeight = height * (newWidth / width);
		}

		if (newHeight > toSizeH) {
			newHeight = toSizeH;
			newWidth = width * (newHeight / height);
		}

		int x = fill ? (int) (toSizeW / 2.0 - newWidth / 2) : 0;
		int y = fill ? (int) (toSizeH / 2.0 - newHeight / 2) : 0;
		int w = (int) newWidth;
		int h = (int) newHeight;

		return resize(x, y, w, h, fill ? toSizeW : w, fill ? toSizeH : h);
	}

	/**
	 * Generates a small preview of this frame with a maximum size of 200x200
	 * 
	 * @return {@link Frame} thumbnail
	 */
	public Frame generateThumbnail() {
		return fitToSize(200, 200, false);
	}

	/**
	 * Returns a copy of this frame resized by a given factor.
	 *
	 * @param factor
	 *            multiplier
	 * @return new {@link Frame}
	 */
	public Frame resize(double factor) {
		if (factor < 0 || factor == 1)
			return copy();

		int w = (int) Math.round(Math.max(getWidth() * factor, 1));
		int h = (int) Math.round(Math.max(getHeight() * factor, 1));

		return resize(w, h);
	}

	/**
	 * Returns a copy of this frame resized to a given width and
	 * height.
	 *
	 * @param w
	 *            width
	 * @param h
	 *            height
	 * @return new {@link Frame} of given dimensions
	 */
	public Frame resize(int w, int h) {
		return resize(0, 0, w, h, w, h);
	}

	/**
	 * Returns a copy of this frame resized to the given width and height, using
	 * offsets and new frame dimensions.
	 *
	 * @param xOffset
	 *            x shift
	 * @param yOffset
	 *            y shift
	 * @param w
	 *            new width
	 * @param h
	 *            new height
	 * @param wSize
	 *            new {@link Frame} width
	 * @param hSize
	 *            new {@link Frame} height
	 * @return new {@link Frame}
	 */
	public synchronized Frame resize(int xOffset, int yOffset, int w, int h, int wSize, int hSize) {
		if (xOffset == 0 && yOffset == 0 && w == width && h == height)
			return copy();

		Frame scaled = new Frame(wSize, hSize);

		int x_ratio = ((width << 16) / w) + 1;
		int y_ratio = ((height << 16) / h) + 1;

		int x2, y2, j, i, t_index, p_index, rat, p;
		for (j = 0; j < hSize; j++) {
			t_index = j * wSize;

			y2 = (((j - yOffset) * y_ratio) >> 16);
			p_index = y2 * width;
			rat = 0;

			for (i = 0; i < wSize; i++) {
				if (i - xOffset < 0 || j - yOffset < 0 || i >= xOffset + w || j >= yOffset + h) {
					scaled.pixels[t_index++] = 0xFF000000;
					continue;
				}

				x2 = (rat >> 16);
				p = p_index + x2;

				if (p >= 0 && p < pixels.length)
					scaled.pixels[t_index] = pixels[p];

				t_index++;
				rat += x_ratio;
			}
		}

		return scaled;
	}

	/**
	 * Gets the width of this {@link Frame}
	 *
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height of this {@link Frame}
	 *
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the pixel value at the given coordinates
	 *
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return ARGB color integer
	 */
	public int get(int x, int y) {
		return pixels[x + y * width];
	}

	/**
	 * Gets the pixel value at the given coordinates
	 *
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @param color
	 *            ARGB integer
	 */
	public void set(int x, int y, int color) {
		pixels[x + y * width] = color;
	}

	/**
	 * Throws an exception if this {@link Frame} has been flagged as disposed
	 * memory.
	 */
	public void assertNotDisposed() {
		if (disposedTime > 0)
			throw new IllegalStateException("Trying to operate on a disposed frame. Unsafe!");
	}

	/**
	 * Gets whether this frame has been marked as disposed.
	 * 
	 * @return {@code true} if this {@link Frame} has been marked for disposal,
	 *         {@code false} if otherwise
	 */
	public boolean isDisposed() {
		return disposedTime > 0;
	}

	/**
	 * Mark this frame as disposed to allow the immediate reuse of its
	 * backing memory. If this frame has already been marked for disposal, this
	 * method does nothing.
	 */
	public synchronized void dispose() {
		if (isDisposed())
			return;

		disposedTime = System.currentTimeMillis();
		DisposedFrameProvider.getInstance().disposeFrame(this);
	}

	protected long getDiposedTime() {
		return disposedTime;
	}

	private int[] getDirtyPixels() {
		return DisposedFrameProvider.getInstance().getDisposedFrame(width, height, false);
	}

	// This is slow and apparently we don't need it? Commenting it out for now
	// public synchronized Frame resize(int toWidth, int toHeight) {
	// checkDisposed();
	//
	// Frame ret = null;
	//
	// if (toWidth > width || toHeight > height) {
	// int scaleUpW = toWidth > width ? toWidth : width;
	// int scaleUpH = toHeight > height ? toHeight : height;
	// ret = scaleUp(scaleUpW, scaleUpH);
	// }
	//
	// if (toHeight < height || toWidth < width) {
	// int scaleDownW = toWidth < width ? toWidth : width;
	// int scaleDownH = toHeight < height ? toHeight : height;
	//
	// if (ret == null)
	// ret = scaleDown(scaleDownW, scaleDownH);
	// else {
	// Frame ret2 = ret.scaleDown(scaleDownW, scaleDownH);
	// ret.dispose();
	// ret = ret2;
	// }
	// }
	//
	// return ret == null ? copy() : ret;
	// }
	//
	// private Frame scaleDown(int w, int h) {
	// if (w > width)
	// w = width;
	// if (h > height)
	// h = height;
	//
	// if (w >= width && h >= height)
	// return copy();
	//
	// float w_factor = width / (float) w;
	// float h_factor = height / (float) h;
	// int w_factor_i = (int) Math.floor(w_factor);
	// int h_factor_i = (int) Math.floor(h_factor);
	//
	// int sample = w_factor_i * h_factor_i;
	//
	// Frame dest = new Frame(w, h);
	//
	// int j, i, srcx, srcy, yi, xi, a, r, g, b, color;
	// double temp_a, temp_r, temp_g, temp_b;
	// for (j = 0; j < h; j++)
	// for (i = 0; i < w; i++) {
	//
	// srcx = (int) Math.floor(i * w_factor);
	// srcy = (int) Math.floor(j * h_factor);
	//
	// temp_a = 0;
	// temp_r = 0;
	// temp_g = 0;
	// temp_b = 0;
	// for (yi = 0; yi < h_factor_i; yi++)
	// for (xi = 0; xi < w_factor_i; xi++) {
	// color = get(srcx + xi, srcy + yi);
	// temp_a += ColorHelper.alpha(color);
	// temp_r += ColorHelper.red(color);
	// temp_g += ColorHelper.green(color);
	// temp_b += ColorHelper.blue(color);
	// }
	//
	// a = (int) (temp_a / sample);
	// r = (int) (temp_r / sample);
	// g = (int) (temp_g / sample);
	// b = (int) (temp_b / sample);
	// dest.set(i, j, ColorHelper.color(a, r, g, b));
	// }
	//
	// return dest;
	// }

}
