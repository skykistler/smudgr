package io.smudgr.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * The {@link DisposedFrameProvider} singleton contains {@link Frame} instances
 * that have been marked as disposed and allows the immediate reuse of their
 * memory.
 * <p>
 * This prevents the JVM garbage collector from working too hard to free and
 * reallocate {@link Frame} instances of the same size that are being used
 * multiple times a second.
 * <p>
 * The disposed frame cache removes {@link Frame} instances that are over a
 * second old.
 */
public class DisposedFrameProvider {

	/**
	 * Gets the current {@link DisposedFrameProvider} instance
	 *
	 * @return {@link DisposedFrameProvider}
	 */
	public static DisposedFrameProvider getInstance() {
		if (instance == null)
			instance = new DisposedFrameProvider();

		return instance;
	}

	private static volatile DisposedFrameProvider instance;

	private HashMap<String, Stack<Frame>> disposed = new HashMap<String, Stack<Frame>>();
	private ArrayList<Frame> toRemove = new ArrayList<Frame>();

	/**
	 * Updates the {@link DisposedFrameProvider}, which removes stale
	 * {@link Frame} instances from the cache.
	 */
	public synchronized void update() {
		for (String key : disposed.keySet()) {
			Stack<Frame> stack = disposed.get(key);

			if (stack == null)
				continue;

			toRemove.clear();
			for (Frame frame : stack)
				if (System.currentTimeMillis() - frame.getDiposedTime() > 10000) {
					toRemove.add(frame);
					frame.pixels = null;
				}

			stack.removeAll(toRemove);
		}
	}

	/**
	 * Get a piece of memory with the given dimensions, optionally clearing it
	 * first. If no disposed {@link Frame} exists to satisfy the dimensions, a
	 * new piece of memory is allocated.
	 *
	 * @param width
	 *            desired width
	 * @param height
	 *            desired height
	 * @param cleanUp
	 *            clear the result
	 * @return {@code int[]}
	 */
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

	/**
	 * Mark a given {@link Frame} as disposed, and ready for reuse.
	 *
	 * @param frame
	 *            to dispose
	 */
	public synchronized void disposeFrame(Frame frame) {
		String hash = getHash(frame.getWidth(), frame.getHeight());

		Stack<Frame> disposedFrames = disposed.get(hash);

		if (disposedFrames == null) {
			disposedFrames = new Stack<Frame>();
			disposed.put(hash, disposedFrames);
		}

		disposedFrames.push(frame);
	}

	private String getHash(int width, int height) {
		return width + ":" + height;
	}
}
