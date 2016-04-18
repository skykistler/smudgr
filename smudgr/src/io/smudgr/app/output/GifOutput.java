package io.smudgr.app.output;

import java.util.ArrayList;

import io.smudgr.app.Controller;
import io.smudgr.project.smudge.source.Frame;

public class GifOutput implements FrameOutput {

	private final static int TARGET_GIF_MS = 50;

	private String path;

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
