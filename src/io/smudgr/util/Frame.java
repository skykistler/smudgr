/**
 * 
 */
package io.smudgr.util;

import io.smudgr.engine.Smudge;

/**
 * {@link Frame} is an abstract data container used to pass a chunk of
 * information to each {@link Smudge}
 */
public abstract class Frame {

	private long disposedTime = 0;

	/**
	 * Mark this frame as disposed to allow the immediate reuse of its
	 * backing memory. If this frame has already been marked for disposal, this
	 * method does nothing.
	 */
	public synchronized void dispose() {
		if (isDisposed())
			return;

		disposedTime = System.currentTimeMillis();
		DisposedFrameProvider.getInstance().disposeFrame(this);
	}

	/**
	 * Creates a duplicate of this {@link Frame}, using new memory space
	 * (or disposed memory if disposed frames of this size exist)
	 *
	 * @return duplicate {@link Frame}
	 */
	public abstract Frame copy();

	/**
	 * Gets whether this frame has been marked as disposed.
	 * 
	 * @return {@code true} if this {@link Frame} has been marked for
	 *         disposal,
	 *         {@code false} if otherwise
	 */
	public boolean isDisposed() {
		return disposedTime > 0;
	}

	protected long getDiposedTime() {
		return disposedTime;
	}

	/**
	 * Throws an exception if this {@link Frame} has been flagged as
	 * disposed memory.
	 */
	public void assertNotDisposed() {
		if (isDisposed())
			throw new IllegalStateException("Trying to operate on a disposed frame. Unsafe!");
	}
}
