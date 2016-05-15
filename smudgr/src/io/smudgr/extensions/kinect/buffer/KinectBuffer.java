package io.smudgr.extensions.kinect.buffer;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Queue;

import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;

import gnu.trove.list.array.TByteArrayList;
import io.smudgr.project.smudge.util.Frame;

public abstract class KinectBuffer {

	protected volatile Queue<Frame> buffer;
	private final int bufferCap = 100;
	protected Device device = null;

	BufferedImage bImage;
	TByteArrayList streamBytes = new TByteArrayList();

	public KinectBuffer(Device dev) {
		device = dev;
	}

	public void start() {
		// Now we can move onto the specific data stream types
		startStream();
	}

	// The parent buffer class will handle how Frames are grabbed
	public Frame getFrame() {
		if (buffer.size() == 0)
			return null;

		return buffer.remove(); // Or poll?
	}

	protected abstract void startStream();

	protected abstract void processByteBuffer(FrameMode mode, ByteBuffer frame, int timestamp);

}
