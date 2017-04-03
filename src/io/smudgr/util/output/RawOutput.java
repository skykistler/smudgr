package io.smudgr.util.output;

import io.smudgr.util.Frame;

/**
 * TODO: Implement outputting to indestructible raw files
 */
public class RawOutput implements FrameOutput {

	@Override
	public int getTargetFPS() {
		return 0;
	}

	@Override
	public void open() {
	}

	@Override
	public void addFrame(Frame f) {
	}

	@Override
	public void close() {
	}

}
