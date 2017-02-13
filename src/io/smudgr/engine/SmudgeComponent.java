/**
 *
 */
package io.smudgr.engine;

import io.smudgr.engine.param.Parametric;

/**
 * {@link SmudgeComponent} types diversify the configuration ability of a
 * {@link Smudge} type by allowing user composition of overall behavior.
 * <p>
 *
 */
public abstract class SmudgeComponent extends Parametric {

	@Override
	public String getTypeName() {
		return "Smudge Component";
	}

	@Override
	public String getTypeIdentifier() {
		return "component";
	}

	/**
	 * Gets the user-recognizable name of the type of {@link SmudgeComponent}
	 * this class represents.
	 *
	 * @return component type name
	 */
	public abstract String getComponentTypeName();

	/**
	 * Gets the unique identifiable string of the type of
	 * {@link SmudgeComponent}
	 * this class represents.
	 *
	 * @return component type identifier
	 */
	public abstract String getComponentTypeIdentifier();

	/**
	 * Gets the unique identifying name of the type of smudge this component
	 * is built for.
	 *
	 * @return {@link String} Smudge type identifier
	 */
	public abstract String getSmudgeTypeIdentifier();

	/**
	 * This method should be implemented to execute any initialization code.
	 * Avoid using a constructor to implement any initialization code, implement
	 * this instead.
	 */
	public void onInit() {

	}

	private Smudge parent;

	/**
	 * Sets the parent {@link Smudge}
	 *
	 * @param smudge
	 *            {@link Smudge}
	 */
	public void setParent(Smudge smudge) {
		parent = smudge;
	}

	/**
	 * Gets the parent {@link Smudge}
	 *
	 * @return {@link Smudge} parent
	 */
	public Smudge getParent() {
		return parent;
	}

}
