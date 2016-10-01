package io.smudgr.project.smudge.param;

public class BooleanParameter extends Parameter {
	
	public String getType() {
		return "boolean";
	}

	private boolean value;

	public BooleanParameter(String name, Parametric parent) {
		this(name, parent, false);
	}

	public BooleanParameter(String name, Parametric parent, boolean initial) {
		super(name, parent);
		setValue(initial);
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

	public void inputOn() {
		if (reverse)
			setValue(false);
		else
			setValue(true);
	}

	public void inputOff() {
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
