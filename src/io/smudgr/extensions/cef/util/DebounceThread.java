package io.smudgr.extensions.cef.util;

public class DebounceThread implements Runnable {

	private long millis;
	private volatile boolean isDebounce;

	public static DebounceThread startDebounce(long millis) {
		DebounceThread t = new DebounceThread(millis);
		t.start();
		return t;
	}

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
			isDebounce = false;
		}
	}

	public boolean isDebouncing() {
		return isDebounce;
	}

}