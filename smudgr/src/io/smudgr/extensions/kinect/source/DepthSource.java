package io.smudgr.extensions.kinect.source;

import io.smudgr.extensions.kinect.KinectExtension;
import io.smudgr.extensions.kinect.buffer.KinectBuffer;

public class DepthSource extends KinectSource {

	@Override
	public KinectBuffer selectKinectBuffer(KinectExtension extension) {
		return extension.getDepthBuffer();
	}

}
