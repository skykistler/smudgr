package io.smudgr.extensions.kinect.source;

import io.smudgr.app.controller.Controller;
import io.smudgr.extensions.kinect.KinectExtension;
import io.smudgr.extensions.kinect.buffer.DepthBuffer;
import io.smudgr.extensions.kinect.buffer.KinectBuffer;
import io.smudgr.extensions.kinect.buffer.VideoBuffer;
import io.smudgr.util.PixelFrame;
import io.smudgr.util.source.Source;

/**
 * The {@link KinectSource} allows using Kinect buffers as an input
 * {@link Source}
 */
public abstract class KinectSource implements Source {

	private KinectBuffer buffer;
	private PixelFrame lastFrame;
	private PixelFrame frame;

	@Override
	public void init() {
		KinectExtension kinectExtension = (KinectExtension) Controller.getInstance().getExtension("Kinect");
		buffer = getKinectBuffer(kinectExtension);
		buffer.start();
	}

	/**
	 * Gets the buffer backing this {@link KinectSource}
	 *
	 * @param extension
	 *            {@link KinectExtension} instance
	 * @return {@link KinectBuffer}
	 * @see VideoBuffer
	 * @see DepthBuffer
	 */
	public abstract KinectBuffer getKinectBuffer(KinectExtension extension);

	@Override
	public synchronized PixelFrame getFrame() {
		frame = buffer.getFrame();
		if (frame == null)
			return lastFrame;

		// Dispose of lastFrame for new lastFrame
		if (lastFrame != null)
			lastFrame.dispose();

		lastFrame = frame;
		return frame;
	}

	@Override
	public PixelFrame getThumbnail() {
		return null;
	}

	@Override
	public void dispose() {
		lastFrame.dispose();
	}

	@Override
	public void update() {
	}

}
