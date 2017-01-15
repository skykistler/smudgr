package io.smudgr.extensions.kinect.source;

import io.smudgr.app.controller.Controller;
import io.smudgr.extensions.kinect.KinectExtension;
import io.smudgr.extensions.kinect.buffer.KinectBuffer;
import io.smudgr.util.Frame;
import io.smudgr.util.source.Source;

public abstract class KinectSource implements Source {

	private KinectBuffer buffer;
	private Frame lastFrame;
	private Frame frame;

	public void init() {
		KinectExtension kinectExtension = (KinectExtension) Controller.getInstance().getExtension("Kinect");
		buffer = getKinectBuffer(kinectExtension);
		buffer.start();
	}

	public abstract KinectBuffer getKinectBuffer(KinectExtension extension);

	public synchronized Frame getFrame() {
		frame = buffer.getFrame();
		if (frame == null)
			return lastFrame;

		// Dispose of lastFrame for new lastFrame
		if (lastFrame != null)
			lastFrame.dispose();

		lastFrame = frame;
		return frame;
	}

	public void dispose() {
		lastFrame.dispose();
	}

	public void update() {
	}

}
