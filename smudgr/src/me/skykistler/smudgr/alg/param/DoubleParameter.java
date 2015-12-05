package me.skykistler.smudgr.alg.param;

import javax.sound.midi.MidiMessage;

public class DoubleParameter implements Parameter {
	private double value;
	private double min;
	private double max;

	public DoubleParameter() {
		this(0);
	}

	public DoubleParameter(double initial) {
		this(initial, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	public DoubleParameter(double initial, double minimum, double maximum) {
		value = initial;
		min = minimum;
		max = maximum;
	}

	public void setValue(double val) {
		value = val;
		enforce();
	}

	public double getValue() {
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
