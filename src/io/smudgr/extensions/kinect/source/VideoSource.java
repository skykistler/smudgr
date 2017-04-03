package io.smudgr.extensions.kinect.source;

import io.smudgr.extensions.kinect.KinectExtension;
import io.smudgr.extensions.kinect.buffer.KinectBuffer;
import io.smudgr.extensions.kinect.buffer.VideoBuffer;
import io.smudgr.util.source.Source;

/**
 * Represents a {@link Source} backed by the {@link VideoBuffer} of the Kinect
 */
public class VideoSource extends KinectSource {

	@Override
	public String getTypeIdentifier() {
		return "kinect-video-feed";
	}

	@Override
	public String getTypeName() {
		return "Kinect Video Feed";
	}

	@Override
	public String getName() {
		return getTypeName();
	}

	@Override
	public KinectBuffer getKinectBuffer(KinectExtension extension) {
		return extension.getVideoBuffer();
	}

}
