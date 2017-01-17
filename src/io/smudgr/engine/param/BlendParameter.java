package io.smudgr.engine.param;

import java.util.ArrayList;

import io.smudgr.engine.alg.math.blend.Blender;

/**
 * The {@link BlendParameter} class is used to parameterize specifically
 * different {@link Blender} functions. This will probably be consolidated into
 * a single 'Function' parameter in the future.
 * 
 * @see UnivariateParameter
 */
public class BlendParameter extends Parameter {

	@Override
	public String getType() {
		return "Blend";
	}

	private ArrayList<Blender> blenders = new ArrayList<Blender>();

	private int current;

	/**
	 * 
	 * @param name
	 *            Identifier
	 * @param parent
	 *            {@link Parametric} parent
	 * @param initial
	 *            {@link Blender} default value
	 * @see BlendParameter#add(Blender)
	 */
	public BlendParameter(String name, Parametric parent, Blender initial) {
		super(name, parent);
		setValue(initial);
	}

	/**
	 * 
	 * @param name
	 *            Identifier
	 * @param parent
	 *            {@link Parametric} parent
	 * @see BlendParameter#add(Blender)
	 */
	public BlendParameter(String name, Parametric parent) {
		super(name, parent);
	}

	@Override
	protected void setValueFromObject(Object o) {
		Blender func = null;

		if (o instanceof Blender)
			func = (Blender) o;

		if (o instanceof String) {
			for (Blender bf : blenders) {
				if (bf.getName().equals(o)) {
					func = bf;
					break;
				}
			}
		}

		if (func == null)
			return;

		add(func);

		setCurrent(blenders.indexOf(func));
	}

	/**
	 * Get the {@link Blender} function currently in use
	 * 
	 * @return {@link Blender} function
	 */
	public Blender getValue() {
		if (blenders.size() == 0)
			return null;

		return blenders.get(current);
	}

	private void setCurrent(int curr) {
		int prev = current;
		current = curr;

		if (prev != current)
			getParent().triggerChange();
	}

	/**
	 * Add a possible {@link Blender} function value.
	 * 
	 * @param func
	 *            {@link Blender}
	 */
	public void add(Blender func) {
		if (!blenders.contains(func))
			blenders.add(func);
	}

	@Override
	public void inputValue(int value) {
		value %= blenders.size();

		setCurrent(value);
	}

	@Override
	public void inputOn() {
	}

	@Override
	public void inputOff() {
	}

	@Override
	public void increment() {
		if (blenders.size() == 0)
			return;

		current++;
		current %= blenders.size();
	}

	@Override
	public void decrement() {
		if (blenders.size() == 0)
			return;

		current--;
		if (current < 0)
			current += blenders.size();
	}

	@Override
	public String getStringValue() {
		if (blenders.size() == 0)
			return "";

		return blenders.get(current).getName();
	}

}
