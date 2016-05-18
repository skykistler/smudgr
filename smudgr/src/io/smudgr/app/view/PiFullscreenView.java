package io.smudgr.app.view;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.tw.pi.framebuffer.FrameBuffer;

import gnu.trove.list.array.TIntArrayList;
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

	private TIntArrayList bg = new TIntArrayList();;
	private int lastFrameW, lastFrameH;

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
	private int x, y, index, i;

	public synchronized void update(Frame frame) {
		fittedFrame = frame.fitToSize(frameBuffer.getWidth(), frameBuffer.getHeight());

		x = frameBuffer.getWidth() / 2 - fittedFrame.getWidth() / 2;
		y = frameBuffer.getHeight() / 2 - fittedFrame.getHeight() / 2;
		index = x + y * frameBuffer.getWidth();

		// if the dimensions have changed, fill with black
		if (lastFrameW != fittedFrame.getWidth() || lastFrameH != fittedFrame.getHeight()) {
			intBuffer.position(0);

			for (i = 0; i < intBuffer.remaining(); i++)
				intBuffer.put(0);
		}
		intBuffer.position(index);

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
