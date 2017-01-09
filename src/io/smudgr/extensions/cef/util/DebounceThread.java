package io.smudgr.extensions.cef.util;

public class DebounceThread implements Runnable {

	private long millis;
	private volatile boolean isDebounce;
	private volatile DebounceCallback callback;

	public DebounceThread(long millis) {
		this.millis = millis;
	}

	public void start() {
		(new Thread(this)).start();
	}

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

	public boolean isDebouncing() {
		return isDebounce;
	}

	public synchronized void setCallback(DebounceCallback callback) {
		this.callback = callback;
	}

}