package io.smudgr.extensions.midi.tcp;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

public class DeviceMessage extends MidiMessage {

	public DeviceMessage(byte[] data, int length) {
		super(null);

		try {
			setMessage(data, length);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	public Object clone() {
		return new DeviceMessage(this.data, this.length);
	}

}
