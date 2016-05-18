package io.smudgr.extensions.kinect.buffer;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;
import org.openkinect.freenect.VideoFormat;
import org.openkinect.freenect.VideoHandler;

import io.smudgr.project.smudge.alg.math.ColorHelper;
import io.smudgr.project.smudge.util.Frame;

public class VideoBuffer extends KinectBuffer {

	Frame imageFrame;

	public VideoBuffer(Device dev) {
		super(dev);
		imageFrame = new Frame(640, 480);
		bImage = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
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

	protected void processByteBuffer(FrameMode mode, ByteBuffer frame, int timestamp) {

		// byte[] imgData = ((DataBufferByte)
		// bImage.getRaster().getDataBuffer()).getData();

		int remaining = frame.remaining();
		int index = 0;
		for (int i = 0; i < remaining; i += 3) {
			// imgData[i] = frame.get(i + 2); // b
			// imgData[i + 1] = frame.get(i + 1); // g
			// imgData[i + 2] = frame.get(i); // r
			imageFrame.pixels[index++] = ColorHelper.color(255, frame.get(i), frame.get(i + 1), frame.get(i + 2));

		}

		// Frame constructor automatically pulls RGB from BufferedImage bImage
		// Added produced Frame to queue for Sources
		addFrame(imageFrame);
	}

}
