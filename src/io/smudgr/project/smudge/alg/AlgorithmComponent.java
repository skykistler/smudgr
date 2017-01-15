package io.smudgr.project.smudge.alg;

import io.smudgr.project.smudge.param.Parametric;
import io.smudgr.project.util.PropertyMap;

public abstract class AlgorithmComponent extends Parametric {
	
	public static final String PROPERTY_MAP_KEY = "component";

	private Algorithm parent;

	public abstract void init();

	public abstract void update();

	public void setAlgorithm(Algorithm a) {
		parent = a;
	}

	public Algorithm getAlgorithm() {
		return parent;
	}

	public void save(PropertyMap pm) {
		super.save(pm);

		pm.setAttribute("type", getType());
		pm.setAttribute("name", getName());
	}

	public abstract String getName();

	public abstract String getType();

	public String toString() {
		return getName();
	}

}
