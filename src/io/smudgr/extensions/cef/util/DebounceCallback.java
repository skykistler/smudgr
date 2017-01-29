package io.smudgr.extensions.cef.util;

/**
 * The {@link DebounceCallback} interface allows passing a callback to a
 * {@link DebounceThread}
 */
public interface DebounceCallback {

	/**
	 * This method is called when the {@link DebounceThread} finishes
	 */
	public void onComplete();
}
