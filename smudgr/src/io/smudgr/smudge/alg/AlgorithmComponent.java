package io.smudgr.smudge.alg;

import io.smudgr.smudge.param.Parametric;

public abstract class AlgorithmComponent extends Parametric {

	private Algorithm parent;
	private int id;

	public abstract void init();

	public abstract void update();

	public void setAlgorithm(Algorithm a) {
		parent = a;

		if (parent.getSmudge() != null)
			setController(parent.getSmudge().getController());
	}

	public Algorithm getAlgorithm() {
		return parent;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

}
