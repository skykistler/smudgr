package io.smudgr.extensions.midi.tcp;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

/**
 * The {@link DeviceMessage} class represents an arbitrary clone-able
 * {@link MidiMessage}
 *
 * @see DeviceMessage#clone()
 */
public class DeviceMessage extends MidiMessage {

	/**
	 * Create a new {@link DeviceMessage} with the given data
	 * 
	 * @param data
	 *            {@code byte[]}
	 * @param length
	 *            of message
	 */
	public DeviceMessage(byte[] data, int length) {
		super(null);

		try {
			setMessage(data, length);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object clone() {
		return new DeviceMessage(this.data, this.length);
	}

}
