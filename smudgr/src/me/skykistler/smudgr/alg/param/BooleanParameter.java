package me.skykistler.smudgr.alg.param;

import javax.sound.midi.MidiMessage;

public class BooleanParameter implements Parameter {
	private boolean value;

	public void setValue(boolean val) {
		value = val;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public void midiInput(MidiMessage message) {
		// TODO Auto-generated method stub

	}

}
