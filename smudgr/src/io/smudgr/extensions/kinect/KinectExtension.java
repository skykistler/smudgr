package io.smudgr.extensions.kinect;

import org.openkinect.freenect.Context;
import org.openkinect.freenect.Device;
import org.openkinect.freenect.Freenect;

import io.smudgr.extensions.ControllerExtension;
import io.smudgr.extensions.kinect.buffer.DepthBuffer;
import io.smudgr.extensions.kinect.buffer.VideoBuffer;
import io.smudgr.project.PropertyMap;

public class KinectExtension implements ControllerExtension {

	private Device device = null;
	private Context ctx = null;
	private DepthBuffer dBuffer;
	private VideoBuffer vBuffer;

	public String getName() {
		return "Kinect";
	}

	public void init() {
		// Open the kinect device through the context
		ctx = Freenect.createContext();
		if (ctx.numDevices() == 0)
			return;

		device = ctx.openDevice(0);

		// Create the buffers but don't start streaming yet
		dBuffer = new DepthBuffer(device);
		vBuffer = new VideoBuffer(device);

		/*-
		 * Now at this point, we have a device ready to open streams for video
		 * and depth.
		 * 
		 * Now would be a good time to this somewhere:
		 * 
		 * dBuffer.start();  //start or whatever
		 * vBuffer.start();
		 * 
		 * Now after being initialized, they are pulling Frames into queues.
		 * These buffers can serve up Frames from the Frame queue in something
		 * like a Source.
		 */
	}

	public DepthBuffer getDepthBuffer() {
		return dBuffer;
	}

	public VideoBuffer getVideoBuffer() {
		return vBuffer;
	}

	public void update() {
	}

	@Override
	public void stop() {
		device.stopDepth();
		device.close();
	}

	@Override
	public void save(PropertyMap pm) {
	}

	@Override
	public void load(PropertyMap pm) {
	}

}
