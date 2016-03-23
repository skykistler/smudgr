package io.smudgr.source.smudge.param;

public class BooleanParameter extends Parameter {

	private Object initial;
	private boolean value;

	public BooleanParameter(String name, Parametric parent) {
		this(name, parent, false);
	}

	public BooleanParameter(String name, Parametric parent, boolean initial) {
		super(name, parent);
		this.initial = initial;
	}

	public void init() {
		setValue(initial);
	}

	public void setInitial(Object o) {
		initial = o;
	}

	public void setValue(Object o) {
		if (o instanceof String)
			value = o.toString().equals("true") ? true : false;
		else
			value = (boolean) o;

		getParent().triggerChange();
	}

	public String getStringValue() {
		return value ? "true" : "false";
	}

	public boolean getValue() {
		return value;
	}

	public void inputValue(int midi) {
		// No need for this
	}

	public void inputOn(int value) {
		if (reverse)
			setValue(false);
		else
			setValue(true);
	}

	public void inputOff(int value) {
		if (reverse)
			setValue(true);
		else
			setValue(false);
	}

	public void increment() {
		setValue(!value);
	}

	public void decrement() {
		increment();
	}

}
