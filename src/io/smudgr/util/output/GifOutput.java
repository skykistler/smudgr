package io.smudgr.util.output;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import io.smudgr.app.controller.Controller;
import io.smudgr.util.Frame;

public class GifOutput implements FrameOutput {

	private final static int TARGET_GIF_MS = 50;

	private String path;

	private int maxWidth, maxHeight;
	private ArrayList<Frame> frames = new ArrayList<Frame>();

	private AnimatedGifEncoder gifEncoder;

	private boolean closed;

	public GifOutput(String name) {
		path = Controller.getInstance().getProject().getOutputPath() + name + "_" + System.currentTimeMillis() + ".gif";
	}

	public int getTargetFPS() {
		return 1000 / TARGET_GIF_MS;
	}

	public void open() {
		gifEncoder = new AnimatedGifEncoder();
		gifEncoder.setDelay(TARGET_GIF_MS);
		gifEncoder.setRepeat(0);
		//		gifEncoder.setQuality();
	}

	public void addFrame(Frame f) {
		if (closed)
			return;

		if (f.getWidth() > maxWidth)
			maxWidth = f.getWidth();
		if (f.getHeight() > maxHeight)
			maxHeight = f.getHeight();

		frames.add(f);
	}

	public void close() {
		if (closed || frames.size() == 0)
			return;

		closed = true;

		(new Thread() {
			public void run() {
				gifEncoder.start(path);

				for (Frame f : frames) {
					BufferedImage frame = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);

					f.drawTo(frame);
					gifEncoder.addFrame(frame);

					f.dispose();
				}

				gifEncoder.finish();
			}
		}).start();
	}

}
