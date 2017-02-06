package io.smudgr.engine.alg;

import io.smudgr.app.project.Project;
import io.smudgr.engine.SmudgeComponent;
import io.smudgr.engine.param.Parametric;

/**
 * The abstract {@link AlgorithmComponent} class defines a generic
 * {@link Parametric} that can be added to a parent {@link Algorithm}.
 * <p>
 * {@link AlgorithmComponent} implementations will be executed by a parent
 * {@link Algorithm} depending on their type (as given by
 * {@link AlgorithmComponent#getComponentIdentifier()}), and are intended to
 * have a behavioral effect on the parent {@link Algorithm}
 */
public abstract class AlgorithmComponent extends SmudgeComponent {

	@Override
	public String getSmudgeIdentifier() {
		return "algorithm";
	}

	private Algorithm parent;

	/**
	 * Called after proper {@link Project} bootstrapping, this method should be
	 * implemented to execute any initialization code. Avoid using a constructor
	 * to implement any initialization code.
	 */
	public abstract void init();

	/**
	 * Called in the application update cycle, per-tick logic should be
	 * implemented here.
	 */
	public abstract void update();

	/**
	 * Sets the parent {@link Algorithm} to act upon.
	 *
	 * @param a
	 *            {@link Algorithm}
	 */
	public void setAlgorithm(Algorithm a) {
		parent = a;
	}

	/**
	 * Gets the parent {@link Algorithm} to act upon.
	 *
	 * @return {@link Algorithm} parent
	 */
	public Algorithm getAlgorithm() {
		return parent;
	}

	@Override
	public String toString() {
		return getName();
	}

}
