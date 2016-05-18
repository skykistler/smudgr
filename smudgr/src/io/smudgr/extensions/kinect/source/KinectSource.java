package io.smudgr.extensions.kinect.source;

import io.smudgr.app.Controller;
import io.smudgr.extensions.kinect.KinectExtension;
import io.smudgr.extensions.kinect.buffer.KinectBuffer;
import io.smudgr.project.smudge.source.Source;
import io.smudgr.project.smudge.util.Frame;

public abstract class KinectSource implements Source {

	private KinectBuffer buffer;
	private Frame lastFrame;
	private Frame frame;

	@Override
	public void init() {
		KinectExtension kinectExtension = (KinectExtension) Controller.getInstance().getExtension("Kinect");
		buffer = getKinectBuffer(kinectExtension);
		buffer.start();
	}

	public abstract KinectBuffer getKinectBuffer(KinectExtension extension);

	@Override
	public synchronized Frame getFrame() {

		frame = buffer.getFrame();
		if (frame == null) {
			return lastFrame;
		}

		// Dispose of lastFrame for new lastFrame
		if (lastFrame != null)
			lastFrame.dispose();

		lastFrame = frame;

		return frame;
	}

	@Override
	public void dispose() {
		lastFrame.dispose();
	}

	@Override
	public void update() {
	}

}
