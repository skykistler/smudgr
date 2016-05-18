package io.smudgr.extensions.kinect.source;

import io.smudgr.extensions.kinect.KinectExtension;
import io.smudgr.extensions.kinect.buffer.KinectBuffer;

public class VideoSource extends KinectSource {

	@Override
	public KinectBuffer getKinectBuffer(KinectExtension extension) {
		return extension.getVideoBuffer();
	}

}
