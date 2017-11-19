package io.smudgr.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * The {@link DisposedFrameProvider} singleton contains {@link PixelFrame} instances
 * that have been marked as disposed and allows the immediate reuse of their
 * memory.
 * <p>
 * This prevents the JVM garbage collector from working too hard to free and
 * reallocate {@link PixelFrame} instances of the same size that are being used
 * multiple times a second.
 * <p>
 * The disposed frame cache removes {@link PixelFrame} instances that are over a
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

	private HashMap<Integer, Stack<PixelFrame>> disposed = new HashMap<Integer, Stack<PixelFrame>>();
	private ArrayList<PixelFrame> toRemove = new ArrayList<PixelFrame>();

	/**
	 * Updates the {@link DisposedFrameProvider}, which removes stale
	 * {@link PixelFrame} instances from the cache.
	 */
	public synchronized void update() {
		for (int key : disposed.keySet()) {
			Stack<PixelFrame> stack = disposed.get(key);

			if (stack == null)
				continue;

			toRemove.clear();
			for (PixelFrame frame : stack)
				if (System.currentTimeMillis() - frame.getDiposedTime() > 10000) {
					toRemove.add(frame);
					frame.pixels = null;
				}

			stack.removeAll(toRemove);
		}
	}

	/**
	 * Get a piece of memory with the given dimensions, optionally clearing it
	 * first. If no disposed {@link PixelFrame} exists to satisfy the dimensions, a
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
		int hash = getHash(width, height);

		// If we don't have any unused frames of the same size, not much we can
		// do
		if (!disposed.containsKey(hash))
			return new int[width * height];

		Stack<PixelFrame> disposedOfEqualSize = disposed.get(hash);

		// No frames to reuse, have to make a new one
		if (disposedOfEqualSize.isEmpty())
			return new int[width * height];

		PixelFrame disposedFrame = disposedOfEqualSize.pop();
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
	 * Mark a given {@link PixelFrame} as disposed, and ready for reuse.
	 *
	 * @param frame
	 *            to dispose
	 */
	public synchronized void disposeFrame(PixelFrame frame) {
		int hash = getHash(frame.getWidth(), frame.getHeight());

		Stack<PixelFrame> disposedFrames = disposed.get(hash);

		if (disposedFrames == null) {
			disposedFrames = new Stack<PixelFrame>();
			disposed.put(hash, disposedFrames);
		}

		disposedFrames.push(frame);
	}

	/**
	 * Dump all disposed frames and notify garbage collector.
	 * Band-aid on the issue of downsample automator quickly allocating many
	 * similarly-sized large frames.
	 */
	public synchronized void dump() {
		for (int key : disposed.keySet()) {
			Stack<PixelFrame> stack = disposed.get(key);

			if (stack == null)
				continue;

			for (PixelFrame frame : stack)
				frame.pixels = null;

			stack.clear();

			stack.removeAll(toRemove);
		}

		System.gc();
	}

	private int getHash(int width, int height) {
		return (width << 16) + height;
	}
}
