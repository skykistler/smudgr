package io.smudgr.extensions.kinect.buffer;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Queue;

import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;

import io.smudgr.project.smudge.util.Frame;

public abstract class KinectBuffer {

	protected volatile Queue<Frame> buffer;
	private final int bufferCap = 100;
	protected Device device = null;

	BufferedImage bImage;

	public KinectBuffer(Device dev) {
		device = dev;
	}

	public void start() {
		// Now we can move onto the specific data stream types
		startStream();
	}

	// The parent buffer class will handle how Frames are grabbed
	public synchronized Frame getFrame() {
		if (buffer.size() == 0)
			return null;

		return buffer.poll();
	}

	protected synchronized void addFrame(Frame frame) {
		if (buffer.size() == bufferCap) {
			getFrame().dispose();
		}
		buffer.add(frame);
	}

	protected abstract void startStream();

	protected abstract void processByteBuffer(FrameMode mode, ByteBuffer frame, int timestamp);

}