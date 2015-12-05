package me.skykistler.smudgr.controller;

import javax.sound.midi.MidiMessage;

public interface DeviceObserver {
	public void midiInput(MidiMessage message);
}
