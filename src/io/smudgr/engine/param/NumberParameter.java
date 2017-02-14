package io.smudgr.engine.param;

import io.smudgr.app.project.util.PropertyMap;

/**
 * The {@link NumberParameter} class is a {@link Parameter} implementation for
 * interacting with a numeric variable.
 * <p>
 * A {@link NumberParameter} instance has a minimum value, a maximum value, and
 * a step property for configuring the amount of change for the
 * {@link NumberParameter#increment()} and {@link NumberParameter#decrement()}
 * events.
 */
public class NumberParameter extends Parameter {

	@Override
	public String getTypeName() {
		return "Number";
	}

	@Override
	public String getTypeIdentifier() {
		return "number";
	}

	private double value;
	private double min;
	private double max;
	private double step;
	private boolean reverse;
	private boolean continuous;

	/**
	 * Instantiate a totally empty {@link NumberParameter}, for reflection.
	 */
	public NumberParameter() {
		super();
	}

	/**
	 * Declare a new {@link NumberParameter} and set the initial value
	 *
	 * @param name
	 *            Name to identify this parameter by.
	 * @param parent
	 *            {@link Parametric} parent of this parameter.
	 * @param initial
	 *            Initial value of the parameter.
	 * @param minimum
	 *            Lowest possible value of the parameter (inclusive)
	 * @param maximum
	 *            Highest possible value of the parameter (inclusive)
	 *
	 * @see NumberParameter#NumberParameter(String, Parametric, double, double,
	 *      double, double) new NumberParemeter(name, parent, initial, minimum,
	 *      maximum, step)
	 */
	public NumberParameter(String name, Parametric parent, double initial, double minimum, double maximum) {
		this(name, parent, initial, minimum, maximum, (maximum - minimum) / 127);
	}

	/**
	 * Declare a new {@link NumberParameter} and set the initial value
	 *
	 * @param name
	 *            Name to identify this parameter by.
	 * @param parent
	 *            {@link Parametric} parent of this parameter.
	 * @param initial
	 *            Initial value of the parameter.
	 * @param minimum
	 *            Lowest possible value of the parameter (inclusive)
	 * @param maximum
	 *            Highest possible value of the parameter (inclusive)
	 * @param step
	 *            Amount of change to occur on an increment or decrement event.
	 *
	 * @see NumberParameter#NumberParameter(String, Parametric, double, double,
	 *      double) new NumberParemeter(name, parent, initial, minimum,
	 *      maximum)
	 */
	public NumberParameter(String name, Parametric parent, double initial, double minimum, double maximum, double step) {
		super(name, parent);

		min = minimum;
		max = maximum;
		setValue(initial);
		setStep(step);
	}

	@Override
	protected void setValueFromObject(Object o) {
		double prevValue = value;

		if (o instanceof Double)
			value = (double) o;
		else
			value = Double.parseDouble(o.toString());

		enforce();

		if (value != prevValue)
			getParent().triggerChange();
	}

	@Override
	public void inputValue(int midi) {
		double m;
		if (reverse)
			m = (127 - midi) / 127.0;
		else
			m = midi / 127.0;

		setValue(m * (max - min));
	}

	@Override
	public void inputOn() {
		setValue(reverse ? min : max);
	}

	@Override
	public void inputOff() {
		setValue(reverse ? max : min);
	}

	/**
	 * Set the lowest possible value of this parameter (inclusive)
	 *
	 * @param minimum
	 *            New minimum
	 * @see NumberParameter#setMax(double)
	 */
	public void setMin(double minimum) {
		if (minimum >= min)
			return;

		double ratio = (value - min) / (max - min);
		min = minimum;

		setValue(ratio * (max - min) + min);
	}

	/**
	 * Set the highest possible value of this parameter (inclusive)
	 *
	 * @param maximum
	 *            New maximum
	 * @see NumberParameter#setMin(double)
	 */
	public void setMax(double maximum) {
		if (maximum <= min)
			return;
		double ratio = (value - min) / (max - min);
		max = maximum;

		setValue(ratio * (max - min) + min);
	}

	/**
	 * Set the amount of change to occur on an increment or decrement event.
	 * <p>
	 * Step must be greater than zero and less than or equal to maximum -
	 * minimum
	 * <p>
	 * Provided value is clamped to that range, so negative values are set to 0
	 * and values higher than maximum - minimum are set to maximum - minimum.
	 *
	 * @param s
	 *            Amount of change
	 * @see NumberParameter#increment()
	 * @see NumberParameter#decrement()
	 */
	public void setStep(double s) {
		s = s > max - min ? max - min : s;
		s = s < 0 ? 0 : s;
		step = s;
	}

	@Override
	public void increment() {
		if (reverse)
			dec();
		else
			inc();

		enforce();
	}

	@Override
	public void decrement() {
		if (reverse) {
			inc();
		} else
			dec();

		enforce();
	}

	private void inc() {
		setValue(value + step);
	}

	private void dec() {
		setValue(value - step);
	}

	private void enforce() {
		if (value < min) {
			if (continuous)
				value += (max - min);
			else
				value = min;
		}

		if (value > max)
			if (continuous)
				value -= (max - min);
			else
				value = max;
	}

	/**
	 * Set the reverse state of this {@link NumberParameter}.
	 * <p>
	 * When this is set to {@code true}, {@link NumberParameter#increment()}
	 * events will decrease the value, and {@link NumberParameter#decrement()}
	 * events will increase the value.
	 * <p>
	 * This is useful when it makes more sense for an increase event to decrease
	 * the actual value, and vice versa.
	 *
	 * @param reverse
	 *            {@code boolean}
	 * @see NumberParameter#setContinuous(boolean)
	 */
	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	/**
	 * Set the continuous state of this {@link NumberParameter}.
	 * <p>
	 * When this is set to {@code true}, increases in the value past the
	 * {@link NumberParameter#getMax()} will cause the value to be set back to
	 * {@link NumberParameter#getMin()}, instead of being clamped. The same is
	 * true if the value decreases past the {@link NumberParameter#getMin()},
	 * the value will be looped back to {@link NumberParameter#getMax()}
	 * <p>
	 * This is useful for when it makes sense for
	 * {@link NumberParameter#increment()} and
	 * {@link NumberParameter#decrement()} events to continue changing the value
	 * instead of hitting a ceiling or floor.
	 *
	 * @param continuous
	 *            {@code boolean}
	 * @see NumberParameter#setReverse(boolean)
	 */
	public void setContinuous(boolean continuous) {
		this.continuous = continuous;
	}

	/**
	 * Gets the highest possible value of this {@link NumberParameter}
	 * (inclusive)
	 *
	 * @return Maximum value
	 * @see NumberParameter#getMin()
	 */
	public double getMax() {
		return max;
	}

	/**
	 * Gets the lowest possible value of this {@link NumberParameter}
	 * (inclusive)
	 *
	 * @return Minimum value
	 * @see NumberParameter#getMax()
	 */
	public double getMin() {
		return min;
	}

	/**
	 * Gets the amount of change that occurs on
	 * {@link NumberParameter#increment()} and
	 * {@link NumberParameter#decrement()} events
	 *
	 * @return Amount of change
	 * @see NumberParameter#getMax()
	 * @see NumberParameter#getMin()
	 */
	public double getStep() {
		return step;
	}

	/**
	 * Gets the current value of this {@link NumberParameter}
	 * <p>
	 * Value may be any decimal between {@link NumberParameter#getMin()} and
	 * {@link NumberParameter#getMax()}.
	 *
	 * @return Numeric value
	 * @see NumberParameter#getStringValue()
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Gets the current value of this {@link NumberParameter} as a string.
	 * <p>
	 * Value may be any decimal between {@link NumberParameter#getMin()} and
	 * {@link NumberParameter#getMax()}, represented as a string.
	 *
	 * @return {@link String} of the numeric value
	 * @see NumberParameter#getValue()
	 */
	@Override
	public String getStringValue() {
		return value + "";
	}

	/**
	 * Casts and returns the current value of this {@link NumberParameter} as an
	 * integer.
	 * <p>
	 * Value may be any integer between {@link NumberParameter#getMin()} and
	 * {@link NumberParameter#getMax()} (inclusive).
	 *
	 * @return Integer value
	 * @see NumberParameter#getValue()
	 */
	public int getIntValue() {
		return (int) value;
	}

	@Override
	public void save(PropertyMap pm) {
		super.save(pm);

		pm.setAttribute("min", getMin());
		pm.setAttribute("max", getMax());
		pm.setAttribute("step", getStep());
	}

	@Override
	public void load(PropertyMap pm) {
		super.load(pm);

		if (pm.hasAttribute("min"))
			setMin(Double.parseDouble(pm.getAttribute("min")));

		if (pm.hasAttribute("max"))
			setMax(Double.parseDouble(pm.getAttribute("max")));

		if (pm.hasAttribute("step"))
			setStep(Double.parseDouble(pm.getAttribute("step")));
	}

}
