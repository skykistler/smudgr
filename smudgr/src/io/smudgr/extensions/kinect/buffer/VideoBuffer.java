package io.smudgr.extensions.kinect.buffer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;
import org.openkinect.freenect.VideoFormat;
import org.openkinect.freenect.VideoHandler;

import io.smudgr.project.smudge.util.Frame;

public class VideoBuffer extends KinectBuffer {

	public VideoBuffer(Device dev) {
		super(dev);
		BufferedImage currentImage = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
	}

	@Override
	public void startStream() {
		device.setVideoFormat(VideoFormat.RGB);

		device.startVideo(new VideoHandler() {
			@Override
			public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {
				processByteBuffer(mode, frame, timestamp);
			}
		});
	}

	/*-
	 * Need to remember to convert from RGB to the smudgr-specific encoding.
	 * 
	 * 1) TByteArrayList needs to reuse memory and reset each processByteBuffers
	 * call.
	 * 
	 * 2) Reading ByteBuffer directly instead of copying into intermediate
	 * array buffer 
	 * 
	 * 3) InputStream is used to convert bytes -> BufferedImage. It needs to be <reset> every time
	 * we are done using it, so we don't waste time making the same sized InputStream.
	 * 
	 */
	@Override
	public void processByteBuffer(FrameMode mode, ByteBuffer frame, int timestamp) {
		if (!frame.hasArray()) {
			return;
		}

		byte[] imgData = ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();
		System.arraycopy(frame.array(), 0, imgData, 0, frame.array().length);

		Frame imageFrame = new Frame(bImage);

		// Frame constructor automatically pulls RGB from BufferedImage bImage
		// Added produced Frame to queue for Sources
		buffer.add(imageFrame);
	}

}
