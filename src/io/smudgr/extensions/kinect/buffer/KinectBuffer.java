package io.smudgr.extensions.kinect.buffer;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;

import io.smudgr.util.Frame;

/**
 * The abstract {@link KinectBuffer} class represents a stream of data being
 * received from the Kinect.
 */
public abstract class KinectBuffer {

	protected volatile Queue<Frame> buffer = new LinkedList<Frame>();
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
	 * Gets the next {@link Frame} from the buffer queue.
	 *
	 * @return {@link Frame} or {@code null} if the queue is empty
	 */
	public synchronized Frame getFrame() {
		if (buffer.size() == 0)
			return null;

		return buffer.poll();
	}

	protected synchronized void addFrame(Frame frame) {
		if (buffer.size() == bufferCap) {
			getFrame().dispose();
		}
		buffer.add(frame);
	}

	protected abstract void startStream();

	protected abstract void processByteBuffer(FrameMode mode, ByteBuffer frame, int timestamp);

}
