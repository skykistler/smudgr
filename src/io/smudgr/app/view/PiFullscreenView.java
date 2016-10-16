package io.smudgr.app.view;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.tw.pi.framebuffer.FrameBuffer;

import io.smudgr.app.threads.ViewThread;
import io.smudgr.project.util.Frame;

public class PiFullscreenView implements View {

	public String getName() {
		return "Linux native framebuffer";
	}

	private int frameBufferNum, lastFrameW, lastFrameH;

	private FrameBuffer frameBuffer;
	private ByteBuffer directBuffer;
	private IntBuffer intBuffer;

	private Window dummyWindow;

	public PiFullscreenView() {
		this(0);
	}

	public PiFullscreenView(int displayNumber) {
		frameBufferNum = displayNumber;

		if (frameBufferNum < 0)
			frameBufferNum = 0;
	}

	public void start(ViewThread thread) {
		try {
			frameBuffer = new FrameBuffer("/dev/fb" + frameBufferNum);

			System.out.println("Linux framebuffer is " + frameBuffer.getWidth() + "x" + frameBuffer.getHeight() + " with a color depth of " + frameBuffer.getColorDepth());

			directBuffer = frameBuffer.getDirectByteBuffer();
			intBuffer = directBuffer.asIntBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}

		dummyWindow = new Window(frameBufferNum);
	}

	// declared to avoid repetitive memory consumption
	private Frame fittedFrame;

	public synchronized void update(Frame frame) {
		fittedFrame = frame.fitToSize(frameBuffer.getWidth(), frameBuffer.getHeight());

		// if the frame dimensions have changed, fill with black
		if (lastFrameW != fittedFrame.getWidth() || lastFrameH != fittedFrame.getHeight()) {
			intBuffer.position(0);

			for (int i = 0; i < intBuffer.remaining(); i++)
				intBuffer.put(0);
		}

		if (frameBuffer.getWidth() != fittedFrame.getWidth() || frameBuffer.getHeight() != fittedFrame.getHeight())
			System.out.println("Fitted frame is not the size of the frame bufer");

		intBuffer.position(0);
		intBuffer.put(fittedFrame.pixels, 0, Math.min(fittedFrame.pixels.length, intBuffer.remaining()));

		frameBuffer.swap();

		lastFrameW = fittedFrame.getWidth();
		lastFrameH = fittedFrame.getHeight();
		fittedFrame.dispose();
	}

	public synchronized void stop() {
		if (frameBuffer != null)
			frameBuffer.close();

		dummyWindow.stop();
	}

}
