package io.smudgr.extensions.kinect.buffer;

import java.nio.ByteBuffer;

import org.openkinect.freenect.DepthFormat;
import org.openkinect.freenect.DepthHandler;
import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;

public class DepthBuffer extends KinectBuffer {

	public DepthBuffer(Device dev) {
		super(dev);
	}

	@Override
	public void startStream() {
		device.setDepthFormat(DepthFormat.REGISTERED);

		device.startDepth(new DepthHandler() {
			@Override
			public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {
				processByteBuffer(mode, frame, timestamp);
			}
		});
	}

	/*
	 * This will be a little trickier because the 2 bytes that represent depth
	 * for every pixel are not the typical color encoding.
	 *
	 */
	@Override
	public void processByteBuffer(FrameMode mode, ByteBuffer frame, int timestamp) {
		if (!frame.hasArray()) {
			return;
		}
		// Do stuff with aforementioned bytes to create frame

	}

}
