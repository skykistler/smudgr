package io.smudgr.model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.smudgr.alg.math.ColorHelper;

public class Frame {
	private BufferedImage image;
	private int width;
	private int height;

	public int[] pixels;

	public Frame(String filename) {
		try {
			load("data/" + filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Frame(int w, int h) {
		width = w;
		height = h;

		init();
	}

	public Frame(BufferedImage image) {
		setBufferedImage(image);
	}

	private void load(String path) throws IOException {
		BufferedImage loaded = ImageIO.read(new File(path));

		width = loaded.getWidth();
		height = loaded.getHeight();

		init();
		convert(loaded);
	}

	private void init() {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		grabPixels();
	}

	private void grabPixels() {
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}

	private void convert(BufferedImage other) {
		// Convert to INT ARGB pixels by drawing to a new image
		Graphics g = image.getGraphics();
		g.drawImage(other, 0, 0, null);
		g.dispose();
	}

	public int get(int x, int y) {
		return pixels[x + y * width];
	}

	public void set(int x, int y, int color) {
		pixels[x + y * width] = color;
	}

	public Frame copy() {
		Frame copy = new Frame(width, height);

		System.arraycopy(pixels, 0, copy.pixels, 0, pixels.length);

		return copy;
	}

	public Frame resize(int w, int h) {
		Frame ret = copy();

		if (w > width || h > height) {
			int scaleUpW = w > width ? w : width;
			int scaleUpH = h > height ? h : height;
			ret = ret.scaleUp(scaleUpW, scaleUpH);
		}

		if (h < height || w < width) {
			int scaleDownW = w < width ? w : width;
			int scaleDownH = h < height ? h : height;
			ret = ret.scaleDown(scaleDownW, scaleDownH);
		}

		return ret;
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

		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++) {

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

		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++) {
				int mx = (int) Math.floor(i * width_factor);
				int my = (int) Math.floor(j * height_factor);

				scaled.set(i, j, get(mx, my));
			}

		return scaled;
	}

	public Frame fitToSize(int sizeW, int sizeH) {
		// Scale down if needed
		if (height > sizeH || width > sizeW) {
			int h = sizeH;
			int w = (int) (width * ((double) sizeH / height));

			return scaleDown(w, h);
		} else
			// Scale up if needed
			if (height < sizeH && width < sizeW) {
			int w = (int) (width * ((double) sizeH / height));
			int h = sizeH;

			return scaleUp(w, h);
		}

		return this;

	}

	public void save(String path) {
		try {
			ImageIO.write(image, "png", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setBufferedImage(BufferedImage img) {
		width = img.getWidth();
		height = img.getHeight();

		int type = img.getType();

		if (type != BufferedImage.TYPE_INT_RGB || type != BufferedImage.TYPE_INT_ARGB) {
			init();
			convert(img);
		} else {
			image = img;
			grabPixels();
		}
	}

	public BufferedImage getBufferedImage() {
		return image;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
