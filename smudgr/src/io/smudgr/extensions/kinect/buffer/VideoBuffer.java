package io.smudgr.extensions.kinect.buffer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;
import org.openkinect.freenect.VideoFormat;
import org.openkinect.freenect.VideoHandler;

import gnu.trove.list.array.TByteArrayList;
import io.smudgr.project.smudge.util.Frame;

public class VideoBuffer extends KinectBuffer {

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

	// Need to remember to fix to reuse objects and correct from RGB
	@Override
	public void processByteBuffer(FrameMode mode, ByteBuffer frame, int timestamp) {
		if (!frame.hasArray()) {
			return;
		}

		TByteArrayList streamBytes = new TByteArrayList();

		final int arrayOffset = frame.arrayOffset();
		for (int i = arrayOffset; i < frame.capacity(); i++) {
			streamBytes.add(frame.get(i));
		}

		// Still have not changed to correct format from RGB
		InputStream in = new ByteArrayInputStream(streamBytes.toArray());
		BufferedImage bImageFromConvert = null;
		try {
			bImageFromConvert = ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Frame imageFrame = new Frame(bImageFromConvert);

		// Added produced Frame to queue for Sources
		buffer.add(imageFrame);
	}

}
