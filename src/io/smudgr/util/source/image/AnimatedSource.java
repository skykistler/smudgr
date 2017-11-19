package io.smudgr.util.source.image;

import io.smudgr.util.source.Source;

/**
 * The {@link AnimatedSource} interface defines behavior for manipulating the
 * playback speed for a source that is changing frames in time with the
 * application.
 * <p>
 * This allows a source to be slowed down or sped up arbitrarily.
 */
public interface AnimatedSource extends Source {

	/**
	 * Sets the playback speed multiplier for this {@link AnimatedSource}
	 *
	 * @param speed
	 *            multiplier
	 */
	public void setSpeed(double speed);

	/**
	 * Gets the playback speed multiplier for this {@link AnimatedSource}
	 *
	 * @return speed
	 *         multiplier
	 */
	public double getSpeed();

}
