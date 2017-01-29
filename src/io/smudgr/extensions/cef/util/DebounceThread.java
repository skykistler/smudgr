package io.smudgr.extensions.cef.util;

/**
 * A {@link DebounceThread} can be used to prevent an action from being executed
 * at a rate that is unnecessary and performance-draining.
 */
public class DebounceThread implements Runnable {

	private long millis;
	private volatile boolean isDebounce;
	private volatile DebounceCallback callback;

	/**
	 * Start a debounce for the specified amount of milliseconds
	 * 
	 * @param millis
	 *            {@code long}
	 */
	public DebounceThread(long millis) {
		this.millis = millis;
	}

	/**
	 * Start the debounce
	 */
	public void start() {
		(new Thread(this)).start();
	}

	@Override
	public void run() {
		isDebounce = true;

		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		} finally {
			if (callback != null) {
				callback.onComplete();
			}

			isDebounce = false;
		}
	}

	/**
	 * Gets whether the thread is still currently waiting/debouncing
	 *
	 * @return {@code boolean}
	 */
	public boolean isDebouncing() {
		return isDebounce;
	}

	/**
	 * Sets the callback to run on debounce completion
	 *
	 * @param callback
	 *            {@link DebounceCallback}
	 */
	public synchronized void setCallback(DebounceCallback callback) {
		this.callback = callback;
	}

}