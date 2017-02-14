package io.smudgr.engine.param;

/**
 * The {@link BooleanParameter} class is a {@link Parameter} implementation for
 * interacting with a boolean (on/off) variable.
 */
public class BooleanParameter extends Parameter {

	@Override
	public String getTypeName() {
		return "Toggle";
	}

	@Override
	public String getTypeIdentifier() {
		return "boolean";
	}

	private boolean value;
	private boolean reverse;

	/**
	 * Instantiate a totally empty {@link BooleanParameter}, for reflection.
	 */
	public BooleanParameter() {
		super();
	}

	/**
	 * Declare a new {@link BooleanParameter} with an initial value of
	 * {@code false}
	 *
	 * @param name
	 *            Name to identify this parameter by.
	 * @param parent
	 *            {@link Parametric} parent of this parameter.
	 *
	 * @see BooleanParameter#BooleanParameter(String, Parametric, boolean) new
	 *      Boolean(name, parent, initial)
	 */
	public BooleanParameter(String name, Parametric parent) {
		this(name, parent, false);
	}

	/**
	 * Declare a new {@link BooleanParameter} and set the initial value.
	 *
	 * @param name
	 *            Name to identify this parameter by.
	 * @param parent
	 *            {@link Parametric} parent of this parameter.
	 * @param initial
	 *            Initial value of this parameter.
	 *
	 * @see BooleanParameter#BooleanParameter(String, Parametric) new
	 *      Boolean(name, parent)
	 */
	public BooleanParameter(String name, Parametric parent, boolean initial) {
		super(name, parent);
		setValue(initial);
	}

	@Override
	protected void setValueFromObject(Object o) {
		if (o instanceof String)
			value = o.toString().equals("true") ? true : false;
		else
			value = (boolean) o;

		getParent().triggerChange();
	}

	@Override
	public String getStringValue() {
		return value ? "true" : "false";
	}

	/**
	 * Return the {@code true} or {@code false} value of this
	 * {@link BooleanParameter}
	 *
	 * @return {@code true} or {@code false}
	 */
	public boolean getValue() {
		return value;
	}

	@Override
	public void inputValue(int midi) {
		// No need for this
	}

	@Override
	public void inputOn() {
		if (reverse)
			setValue(false);
		else
			setValue(true);
	}

	@Override
	public void inputOff() {
		if (reverse)
			setValue(true);
		else
			setValue(false);
	}

	@Override
	public void increment() {
		setValue(!value);
	}

	@Override
	public void decrement() {
		increment();
	}

	/**
	 * Set the reverse state of this {@link BooleanParameter}.
	 * <p>
	 * When this is set to {@code true}, {@link NumberParameter#inputOn()}
	 * events will set the value to false, and
	 * {@link NumberParameter#inputOff()} events will set the value to true.
	 * <p>
	 * This is useful when it makes more sense for an input-on event to falsify
	 * the actual value, and vice versa.
	 *
	 * @param reverse
	 *            {@code boolean}
	 */
	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

}
