package io.smudgr.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class DisposedFrameProvider {

	public static DisposedFrameProvider getInstance() {
		if (instance == null)
			instance = new DisposedFrameProvider();

		return instance;
	}

	private static volatile DisposedFrameProvider instance;

	private HashMap<String, Stack<Frame>> disposed = new HashMap<String, Stack<Frame>>();

	public synchronized void update() {
		for (String key : disposed.keySet()) {
			Stack<Frame> stack = disposed.get(key);

			if (stack == null)
				continue;

			ArrayList<Frame> toRemove = new ArrayList<Frame>();
			for (Frame frame : stack)
				if (System.currentTimeMillis() - frame.disposedTime > 1000)
					toRemove.add(frame);

			stack.removeAll(toRemove);
		}
	}

	public synchronized int[] getDisposedFrame(int width, int height, boolean cleanUp) {
		String hash = getHash(width, height);

		// If we don't have any unused frames of the same size, not much we can
		// do
		if (!disposed.containsKey(hash))
			return new int[width * height];

		Stack<Frame> disposedOfEqualSize = disposed.get(hash);

		// No frames to reuse, have to make a new one
		if (disposedOfEqualSize.isEmpty())
			return new int[width * height];

		Frame disposedFrame = disposedOfEqualSize.pop();
		int[] pixels = disposedFrame.pixels;

		// If the caller doesn't like dirty data, we have to use a O(n)
		// operation to clear it
		if (cleanUp)
			for (int j = 0; j < height; j++)
				for (int i = 0; i < width; i++)
					pixels[i + j * width] = 0;

		return pixels;
	}

	public synchronized void disposeFrame(Frame frame) {
		String hash = getHash(frame.getWidth(), frame.getHeight());

		Stack<Frame> disposedFrames = disposed.get(hash);

		if (disposedFrames == null) {
			disposedFrames = new Stack<Frame>();
			disposed.put(hash, disposedFrames);
		}

		disposedFrames.push(frame);
	}

	public String getHash(int width, int height) {
		return width + ":" + height;
	}
}
