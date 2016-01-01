package io.smudgr.controller.device;

import javax.sound.midi.MidiMessage;

public interface DeviceObserver {
	public void midiInput(MidiMessage message);
}
