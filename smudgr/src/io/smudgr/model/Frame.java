package io.smudgr.model;

public class Frame {
	private int width;
	private int height;

	public int[] pixels;

	public Frame(String filename) {
		System.out.println("IMPLEMENT LOAD IMAGE!!!!");
	}

	public Frame(int w, int h) {
		width = w;
		height = h;
	}

	public int get(int x, int y) {
		return pixels[x + y * width];
	}

	public void set(int x, int y, int color) {
		pixels[x + y * width] = color;
	}

	public Frame copy() {
		Frame copy = new Frame(width, height);

		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				copy.set(i, j, get(i, j));

		return copy;
	}

	public void resize(int w, int h) {
		if (w > width || h > height) {
			int scaleUpW = w > width ? w : width;
			int scaleUpH = h > height ? h : height;
			scaleUp(scaleUpW, scaleUpH);
		}

		if (h < height || w < width) {
			int scaleDownW = w < width ? w : width;
			int scaleDownH = h < height ? h : height;
			scaleDown(scaleDownW, scaleDownH);
		}
	}

	private void scaleDown(int w, int h) {
		// TODO IMPLEMENT THIS
		if (w > width || h > height)
			return;

		System.out.println("IMPLEMENT SCALE DOWN");
		width = w;
		height = h;
	}

	private void scaleUp(int w, int h) {
		if (w < width || h < height)
			return;

		double width_factor = (double) width / w;
		double height_factor = (double) height / h;

		Frame scaled = new Frame(w, h);

		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++) {
				int mx = (int) Math.floor(i * width_factor);
				int my = (int) Math.floor(j * height_factor);
				int index = mx + my * width;
				if (index >= pixels.length)
					continue;
				scaled.pixels[i + j * width] = pixels[index];
			}

		width = w;
		height = h;
		pixels = scaled.pixels;
	}

	public void save(String path) {
		System.out.println("IMPLEMENT FRAME SAVING!!!");
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
