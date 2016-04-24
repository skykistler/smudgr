package io.smudgr.project.smudge.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import io.smudgr.project.smudge.alg.math.ColorHelper;

public class Frame {
	private int width;
	private int height;

	public int[] pixels;

	public boolean disposed = false;

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

	public Frame copy() {
		checkDisposed();

		Frame copy = new Frame(width, height);

		System.arraycopy(pixels, 0, copy.pixels, 0, pixels.length);

		return copy;
	}

	public void copyTo(Frame f) {
		checkDisposed();

		System.arraycopy(pixels, 0, f.pixels, 0, Math.min(f.pixels.length, pixels.length));
	}

	public void drawTo(BufferedImage image) {
		checkDisposed();

		Frame fittedFrame = fitToSize(image.getWidth(), image.getHeight());

		int x = image.getWidth() / 2 - fittedFrame.getWidth() / 2;
		int y = image.getHeight() / 2 - fittedFrame.getHeight() / 2;

		int[] imageRaster = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		// TODO: one day this can draw rectangles around the target area
		for (int j = 0; j < image.getHeight(); j++)
			for (int i = 0; i < image.getWidth(); i++) {
				imageRaster[i + j * image.getWidth()] = 0;
			}
		try {
			image.setRGB(x, y, fittedFrame.getWidth(), fittedFrame.getHeight(), fittedFrame.pixels, 0, fittedFrame.getWidth());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("x: " + x);
			System.out.println("y: " + y);
			System.out.println("orig width: " + width);
			System.out.println("orig height: " + height);
			System.out.println("to width: " + image.getWidth());
			System.out.println("to height: " + image.getHeight());
			System.out.println("fitted width: " + fittedFrame.getWidth());
			System.out.println("fitted height: " + fittedFrame.getHeight());
		}
	}

	public Frame resize(int toWidth, int toHeight) {
		checkDisposed();

		Frame ret = null;

		if (toWidth > width || toHeight > height) {
			int scaleUpW = toWidth > width ? toWidth : width;
			int scaleUpH = toHeight > height ? toHeight : height;
			ret = scaleUp(scaleUpW, scaleUpH);
		}

		if (toHeight < height || toWidth < width) {
			int scaleDownW = toWidth < width ? toWidth : width;
			int scaleDownH = toHeight < height ? toHeight : height;

			if (ret == null)
				ret = scaleDown(scaleDownW, scaleDownH);
			else
				ret = ret.scaleDown(scaleDownW, scaleDownH);
		}

		return ret == null ? copy() : ret;
	}

	public Frame fitToSize(int toSizeW, int toSizeH) {
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

		return resize((int) Math.floor(newWidth), (int) Math.floor(newHeight));
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
		if (disposed)
			throw new IllegalStateException("Trying to operate on a disposed frame. Unsafe!");
	}

	public void dispose() {
		disposed = true;
		DisposedFrameProvider.getInstance().disposeFrame(this);
	}

	private int[] getDirtyPixels() {
		return DisposedFrameProvider.getInstance().getDisposedFrame(width, height, false);
	}

	private Frame scaleDown(int w, int h) {
		if (w > width)
			w = width;
		if (h > height)
			h = height;

		float w_factor = width / (float) w;
		float h_factor = height / (float) h;
		int w_factor_i = (int) Math.floor(w_factor);
		int h_factor_i = (int) Math.floor(h_factor);

		int sample = w_factor_i * h_factor_i;

		Frame dest = new Frame(w, h);

		for (int j = 0; j < h; j++)
			for (int i = 0; i < w; i++) {

				int srcx = (int) Math.floor(i * w_factor);
				int srcy = (int) Math.floor(j * h_factor);

				double temp_a = 0;
				double temp_r = 0;
				double temp_g = 0;
				double temp_b = 0;
				for (int yi = 0; yi < h_factor_i; yi++)
					for (int xi = 0; xi < w_factor_i; xi++) {
						int color = get(srcx + xi, srcy + yi);
						temp_a += ColorHelper.alpha(color);
						temp_r += ColorHelper.red(color);
						temp_g += ColorHelper.green(color);
						temp_b += ColorHelper.blue(color);
					}

				int a = (int) (temp_a / sample);
				int r = (int) (temp_r / sample);
				int g = (int) (temp_g / sample);
				int b = (int) (temp_b / sample);
				dest.set(i, j, ColorHelper.color(a, r, g, b));
			}

		return dest;
	}

	private Frame scaleUp(int w, int h) {
		if (w < width)
			w = width;
		if (h < height)
			h = height;

		double width_factor = (double) width / w;
		double height_factor = (double) height / h;

		Frame scaled = new Frame(w, h);

		for (int j = 0; j < h; j++)
			for (int i = 0; i < w; i++) {
				int mx = (int) Math.floor(i * width_factor);
				int my = (int) Math.floor(j * height_factor);

				scaled.set(i, j, get(mx, my));
			}

		return scaled;
	}

}
