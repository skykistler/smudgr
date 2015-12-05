package me.skykistler.smudgr.alg.param;

import javax.sound.midi.MidiMessage;

public class IntegerParameter implements Parameter {
	private int value;
	private int min;
	private int max;

	public IntegerParameter() {
		this(0);
	}

	public IntegerParameter(int initial) {
		this(initial, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public IntegerParameter(int initial, int minimum, int maximum) {
		value = initial;
		min = minimum;
		max = maximum;
	}

	public void setValue(int val) {
		value = val;
		enforce();
	}

	public int getValue() {
		return value;
	}

	// TODO make continuous/wrap mode, instead of truncating
	private void enforce() {
		value = value < min ? min : value;
		value = value > max ? max : value;
	}

	@Override
	public void midiInput(MidiMessage message) {
		// TODO Auto-generated method stub

	}

}
