package io.smudgr.ext.midi;

import javax.sound.midi.MidiMessage;

public interface DeviceObserver {
	public void midiInput(MidiMessage message, long timestamp);
}