package io.smudgr.extensions.kinect.buffer;

import java.nio.ByteBuffer;

import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;
import org.openkinect.freenect.VideoFormat;
import org.openkinect.freenect.VideoHandler;

import io.smudgr.extensions.image.alg.math.ColorHelper;
import io.smudgr.util.PixelFrame;

/**
 * Processes and stores the video information received from the Kinect
 */
public class VideoBuffer extends KinectBuffer {

	/**
	 * Creates a new {@link VideoBuffer} with the given Kinect {@link Device}
	 *
	 * @param dev
	 *            {@link Device}
	 */
	public VideoBuffer(Device dev) {
		super(dev);
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
	protected void processByteBuffer(FrameMode mode, ByteBuffer frame, int timestamp) {

		// byte[] imgData = ((DataBufferByte)
		// bImage.getRaster().getDataBuffer()).getData();

		PixelFrame imageFrame = new PixelFrame(640, 480);

		int remaining = frame.remaining();
		int index = 0;
		for (int i = 0; i < remaining; i += 3) {
			// imgData[i] = frame.get(i + 2); // b
			// imgData[i + 1] = frame.get(i + 1); // g
			// imgData[i + 2] = frame.get(i); // r
			imageFrame.pixels[index++] = ColorHelper.color(255, Byte.toUnsignedInt(frame.get(i)),
					Byte.toUnsignedInt(frame.get(i + 1)), Byte.toUnsignedInt(frame.get(i + 2)));

		}

		// Frame constructor automatically pulls RGB from BufferedImage bImage
		// Added produced Frame to queue for Sources
		addFrame(imageFrame);
	}

}
