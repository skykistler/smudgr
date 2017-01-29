package io.smudgr.extensions.kinect.source;

import io.smudgr.extensions.kinect.KinectExtension;
import io.smudgr.extensions.kinect.buffer.DepthBuffer;
import io.smudgr.extensions.kinect.buffer.KinectBuffer;
import io.smudgr.util.source.Source;

/**
 * Represents a {@link Source} backed by the {@link DepthBuffer} of the Kinect
 */
public class DepthSource extends KinectSource {

	@Override
	public KinectBuffer getKinectBuffer(KinectExtension extension) {
		return extension.getDepthBuffer();
	}

}
