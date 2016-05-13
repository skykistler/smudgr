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
	DepthBuffer dBuffer;
	VideoBuffer vBuffer;

	public String getName() {
		return "Kinect";
	}

	public void init() {
		// Open the kinect device through the context
		ctx = Freenect.createContext();
		if (ctx.numDevices() > 0) {
			device = ctx.openDevice(0);
		} else {
			System.err.println("No kinects detected.  Exiting.");
			System.exit(0);
		}

		/*-
		 * Now at this point, we have a device ready to open streams for video
		 * and depth.
		 * 
		 * Now would be a good time to this somewhere:
		 * 
		 * DepthBuffer dBuffer = new DepthBuffer(device);
		 * VideoBuffer vBuffer = new VideoBuffer(device);
		 * dBuffer.init();  //start or whatever
		 * vBuffer.init();
		 * 
		 * Now after being initialized, they are pulling Frames into queues.
		 * These buffers can serve up Frames from the Frame queue in something
		 * like a Source.
		 * 
		 */

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(PropertyMap pm) {
		// TODO Auto-generated method stub

	}

	@Override
	public void load(PropertyMap pm) {
		// TODO Auto-generated method stub

	}

}
