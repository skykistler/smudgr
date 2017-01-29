package io.smudgr.extensions.midi.messages;

import io.smudgr.app.controller.Controllable;

/**
 * The {@link MidiMessageStrategy} interface allows creating custom behavior for
 * different types of MIDI signals. Each strategy is statically set to a given
 * MIDI message type ID, and
 * {@link MidiMessageStrategy#input(Controllable, int)} is called when a message
 * with that ID is received.
 */
public interface MidiMessageStrategy {
	/**
	 * This method is called with the bound {@link Controllable} when a MIDI
	 * message corresponding to this strategy is received
	 *
	 * @param c
	 *            Bound {@link Controllable}
	 * @param value
	 *            Secondary data value (such as velocity)
	 */
	public void input(Controllable c, int value);
}
