package io.smudgr.app.view;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.tw.pi.framebuffer.FrameBuffer;

import io.smudgr.project.smudge.util.Frame;

public class PiFullscreenView implements View {

	public String getName() {
		return "Linux native framebuffer";
	}

	private int frameBufferNum;

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

	public void start() {
		try {
			frameBuffer = new FrameBuffer("/dev/fb" + frameBufferNum);
			directBuffer = frameBuffer.getDirectByteBuffer();
			intBuffer = directBuffer.asIntBuffer();

			System.out.println("Frame color depth: " + frameBuffer.getColorDepth());
		} catch (IOException e) {
			e.printStackTrace();
		}

		dummyWindow = new Window(frameBufferNum);
	}

	int x, y;

	public synchronized void update(Frame frame) {
		Frame fittedFrame = frame.fitToSize(frameBuffer.getWidth(), frameBuffer.getHeight());

		x = frameBuffer.getWidth() / 2 - fittedFrame.getWidth() / 2;
		y = frameBuffer.getHeight() / 2 - fittedFrame.getHeight() / 2;

		intBuffer.position(x + y * frameBuffer.getWidth());
		intBuffer.put(fittedFrame.pixels, 0, Math.min(fittedFrame.pixels.length, intBuffer.remaining()));

		frameBuffer.swap();

		fittedFrame.dispose();
	}

	public synchronized void stop() {
		if (frameBuffer != null)
			frameBuffer.close();

		dummyWindow.stop();
	}

}
