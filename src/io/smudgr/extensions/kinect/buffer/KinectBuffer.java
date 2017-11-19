package io.smudgr.extensions.kinect.buffer;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;

import io.smudgr.util.PixelFrame;

/**
 * The abstract {@link KinectBuffer} class represents a stream of data being
 * received from the Kinect.
 */
public abstract class KinectBuffer {

	protected volatile Queue<PixelFrame> buffer = new LinkedList<PixelFrame>();
	private final int bufferCap = 100;
	protected Device device = null;

	/**
	 * Create a new {@link KinectBuffer} using the given Kinect {@link Device}
	 *
	 * @param dev
	 *            {@link Device}
	 */
	public KinectBuffer(Device dev) {
		device = dev;
	}

	/**
	 * Start the data stream from the kinect
	 */
	public void start() {
		// Now we can move onto the specific data stream types
		startStream();
	}

	/**
	 * Gets the next {@link PixelFrame} from the buffer queue.
	 *
	 * @return {@link PixelFrame} or {@code null} if the queue is empty
	 */
	public synchronized PixelFrame getFrame() {
		if (buffer.size() == 0)
			return null;

		return buffer.poll();
	}

	protected synchronized void addFrame(PixelFrame frame) {
		if (buffer.size() == bufferCap) {
			getFrame().dispose();
		}
		buffer.add(frame);
	}

	protected abstract void startStream();

	protected abstract void processByteBuffer(FrameMode mode, ByteBuffer frame, int timestamp);

}
