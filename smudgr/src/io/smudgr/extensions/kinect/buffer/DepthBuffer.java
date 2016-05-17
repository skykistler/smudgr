package io.smudgr.extensions.kinect.buffer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

import org.openkinect.freenect.DepthFormat;
import org.openkinect.freenect.DepthHandler;
import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;

import io.smudgr.project.smudge.util.Frame;

public class DepthBuffer extends KinectBuffer {

	public DepthBuffer(Device dev) {
		super(dev);
		bImage = new BufferedImage(640, 480, BufferedImage.TYPE_USHORT_GRAY);
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
	 */
	@Override
	public void processByteBuffer(FrameMode mode, ByteBuffer frame, int timestamp) {
		byte[] imgData = ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();

		for (int i = 0; i < frame.remaining() && i < imgData.length; i++) {
			imgData[i] = frame.get(i);
		}

		addFrame(new Frame(bImage));

	}

}
