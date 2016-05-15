package io.smudgr.extensions.kinect.buffer;

import java.awt.image.BufferedImage;
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
		if (!frame.hasArray()) {
			return;
		}

		/*- I think we can do it this way:
		 * 
		 * (1)
		 * 
		 * DataBufferByte dataBuffer = new DataBufferByte(frame.array(),
		 * frame.capacity()); SampleModel sampleModel = new
		 * BandedSampleModel(dataBuffer.getDataType(), 640, 480, 1); Raster
		 * raster = Raster.createRaster(sampleModel, dataBuffer, null);
		 * bImage.setData(raster);
		 * 
		 * Or this way:
		 * 
		 * (2)
		 * 
		 * byte[] imgData = ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();
		 * System.arraycopy(frame.array(), 0, imgData, 0, frame.array().length);
		 * 
		 * Option 2 seems the most succinct.
		 * 
		 */

		Frame imageFrame = new Frame(bImage);

		// Frame constructor automatically pulls RGB from BufferedImage bImage
		// Added produced Frame to queue for Sources
		buffer.add(imageFrame);

	}

}
