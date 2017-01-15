package io.smudgr.extensions.kinect;

import org.openkinect.freenect.Context;
import org.openkinect.freenect.Device;
import org.openkinect.freenect.Freenect;

import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.extensions.ControllerExtension;
import io.smudgr.extensions.kinect.buffer.DepthBuffer;
import io.smudgr.extensions.kinect.buffer.VideoBuffer;

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
		try {
			ctx = Freenect.createContext();
			if (ctx.numDevices() == 0)
				return;
		} catch (UnsatisfiedLinkError e) {
			return;
		}

		System.out.println("Kinect Extension initialized...");
		device = ctx.openDevice(0);

		System.out.println("Kinect device opened...");

		// Create the buffers but don't start streaming yet
		dBuffer = new DepthBuffer(device);
		vBuffer = new VideoBuffer(device);

		System.out.println("Kinect buffers initialized...");

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
		if (device == null)
			return;

		device.stopDepth();
		device.close();
	}

	public void sendMessage(ApiMessage message) {
	}

	@Override
	public void save(PropertyMap pm) {
	}

	@Override
	public void load(PropertyMap pm) {
	}

}
