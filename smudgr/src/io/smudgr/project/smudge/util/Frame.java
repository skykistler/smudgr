package io.smudgr.project.smudge.util;

import java.awt.image.BufferedImage;

public class Frame {
	private int width;
	private int height;

	public int[] pixels;

	public long disposedTime = 0;

	public Frame(int w, int h) {
		width = w;
		height = h;

		pixels = getDirtyPixels();
	}

	public Frame(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();

		pixels = getDirtyPixels();

		image.getRGB(0, 0, width, height, pixels, 0, width);
	}

	public synchronized Frame copy() {
		checkDisposed();

		Frame copy = new Frame(width, height);

		System.arraycopy(pixels, 0, copy.pixels, 0, pixels.length);

		return copy;
	}

	public synchronized void copyTo(Frame f) {
		checkDisposed();

		System.arraycopy(pixels, 0, f.pixels, 0, Math.min(f.pixels.length, pixels.length));
	}

	public synchronized void drawTo(BufferedImage image) {
		checkDisposed();

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

	public synchronized Frame fitToSize(int toSizeW, int toSizeH) {
		checkDisposed();

		double newWidth = width;
		double newHeight = height;

		boolean tooSmall = newWidth < toSizeW && newHeight < toSizeH;

		if (width > toSizeW || tooSmall) {
			newWidth = toSizeW;
			newHeight = (newWidth * height) / width;
		}

		if (newHeight > toSizeH) {
			newHeight = toSizeH;
			newWidth = (newHeight * width) / height;
		}

		int x = (int) (toSizeW / 2.0 - newWidth / 2);
		int y = (int) (toSizeH / 2.0 - newHeight / 2);
		int w = (int) newWidth;
		int h = (int) newHeight;

		return resize(x, y, w, h, toSizeW, toSizeH);
	}

	public synchronized Frame resize(int w, int h) {
		return resize(0, 0, w, h, w, h);
	}

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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int get(int x, int y) {
		return pixels[x + y * width];
	}

	public void set(int x, int y, int color) {
		pixels[x + y * width] = color;
	}

	public void checkDisposed() {
		if (disposedTime > 0)
			throw new IllegalStateException("Trying to operate on a disposed frame. Unsafe!");
	}

	public synchronized void dispose() {
		disposedTime = System.currentTimeMillis();
		DisposedFrameProvider.getInstance().disposeFrame(this);
	}

	private int[] getDirtyPixels() {
		return DisposedFrameProvider.getInstance().getDisposedFrame(width, height, false);
	}

	// This is slow and apparently we don't need it? Commenting it out for now
	//	public synchronized Frame resize(int toWidth, int toHeight) {
	//		checkDisposed();
	//
	//		Frame ret = null;
	//
	//		if (toWidth > width || toHeight > height) {
	//			int scaleUpW = toWidth > width ? toWidth : width;
	//			int scaleUpH = toHeight > height ? toHeight : height;
	//			ret = scaleUp(scaleUpW, scaleUpH);
	//		}
	//
	//		if (toHeight < height || toWidth < width) {
	//			int scaleDownW = toWidth < width ? toWidth : width;
	//			int scaleDownH = toHeight < height ? toHeight : height;
	//
	//			if (ret == null)
	//				ret = scaleDown(scaleDownW, scaleDownH);
	//			else {
	//				Frame ret2 = ret.scaleDown(scaleDownW, scaleDownH);
	//				ret.dispose();
	//				ret = ret2;
	//			}
	//		}
	//
	//		return ret == null ? copy() : ret;
	//	}
	//
	//	private Frame scaleDown(int w, int h) {
	//		if (w > width)
	//			w = width;
	//		if (h > height)
	//			h = height;
	//
	//		if (w >= width && h >= height)
	//			return copy();
	//
	//		float w_factor = width / (float) w;
	//		float h_factor = height / (float) h;
	//		int w_factor_i = (int) Math.floor(w_factor);
	//		int h_factor_i = (int) Math.floor(h_factor);
	//
	//		int sample = w_factor_i * h_factor_i;
	//
	//		Frame dest = new Frame(w, h);
	//
	//		int j, i, srcx, srcy, yi, xi, a, r, g, b, color;
	//		double temp_a, temp_r, temp_g, temp_b;
	//		for (j = 0; j < h; j++)
	//			for (i = 0; i < w; i++) {
	//
	//				srcx = (int) Math.floor(i * w_factor);
	//				srcy = (int) Math.floor(j * h_factor);
	//
	//				temp_a = 0;
	//				temp_r = 0;
	//				temp_g = 0;
	//				temp_b = 0;
	//				for (yi = 0; yi < h_factor_i; yi++)
	//					for (xi = 0; xi < w_factor_i; xi++) {
	//						color = get(srcx + xi, srcy + yi);
	//						temp_a += ColorHelper.alpha(color);
	//						temp_r += ColorHelper.red(color);
	//						temp_g += ColorHelper.green(color);
	//						temp_b += ColorHelper.blue(color);
	//					}
	//
	//				a = (int) (temp_a / sample);
	//				r = (int) (temp_r / sample);
	//				g = (int) (temp_g / sample);
	//				b = (int) (temp_b / sample);
	//				dest.set(i, j, ColorHelper.color(a, r, g, b));
	//			}
	//
	//		return dest;
	//	}

}
