package io.smudgr.record;

import java.util.ArrayList;

import io.smudgr.smudge.source.Frame;

public class GifOutput implements FrameOutput {
	public final static int TARGET_GIF_MS = 50;

	private String path;

	private ArrayList<Frame> frames = new ArrayList<Frame>();

	private AnimatedGifEncoder gifEncoder;

	private boolean closed;

	public GifOutput(String path) {
		this.path = path;
	}

	public void open() {
		gifEncoder = new AnimatedGifEncoder();
		gifEncoder.setDelay(TARGET_GIF_MS);
		gifEncoder.setRepeat(1);
		//		gifEncoder.setQuality();
	}

	public void addFrame(Frame f) {
		if (closed)
			return;

		frames.add(f);
	}

	public void close() {
		if (closed || frames.size() == 0)
			return;

		closed = true;

		(new Thread() {
			public void run() {
				gifEncoder.start(path);

				for (Frame f : frames)
					gifEncoder.addFrame(f.getBufferedImage());

				gifEncoder.finish();
			}
		}).start();
	}

}
