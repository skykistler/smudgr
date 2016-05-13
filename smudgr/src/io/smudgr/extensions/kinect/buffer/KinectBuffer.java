package io.smudgr.extensions.kinect.buffer;

import java.nio.ByteBuffer;
import java.util.Queue;

import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;

import io.smudgr.project.smudge.util.Frame;

public abstract class KinectBuffer {

	protected volatile Queue<Frame> buffer;
	private final int bufferCap = 100;
	protected Device device = null;

	public KinectBuffer(Device dev) {
		device = dev;
	}

	public void init() {
		// Now we can move onto the specific data stream types through abstract
		// methods
		startStream();
	}

	public void stop() {
		// These need to be called when buffer killed
		if (device != null) {
			device.stopDepth();
			device.close();
		}
	}

	// The parent buffer class will handle how Frames are grabbed
	public Frame getFrame() {
		return buffer.remove(); // Or poll?
	}

	protected abstract void startStream();

	protected abstract void processByteBuffer(FrameMode mode, ByteBuffer frame, int timestamp);

}
