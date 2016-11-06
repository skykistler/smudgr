package io.smudgr.project.smudge.param;

import java.util.ArrayList;

import io.smudgr.project.smudge.alg.math.blend.Blender;

public class BlendParameter extends Parameter {

	public String getType() {
		return "Blend";
	}

	private ArrayList<Blender> blenders = new ArrayList<Blender>();

	private int current;

	public BlendParameter(String name, Parametric parent, Blender initial) {
		super(name, parent);
		setValue(initial);
	}

	public BlendParameter(String name, Parametric parent) {
		super(name, parent);
	}

	public void update() {
	}

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

	public void add(Blender func) {
		if (!blenders.contains(func))
			blenders.add(func);
	}

	public void inputValue(int value) {
		value %= blenders.size();

		setCurrent(value);
	}

	public void inputOn() {
	}

	public void inputOff() {
	}

	public void increment() {
		if (blenders.size() == 0)
			return;

		current++;
		current %= blenders.size();
	}

	public void decrement() {
		if (blenders.size() == 0)
			return;

		current--;
		if (current < 0)
			current += blenders.size();
	}

	public String getStringValue() {
		if (blenders.size() == 0)
			return "";

		return blenders.get(current).getName();
	}

}
