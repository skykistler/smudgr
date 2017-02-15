/**
 *
 */
package io.smudgr.engine;

import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.param.Parametric;

/**
 * {@link SmudgeComponent} types diversify the configuration ability of a
 * {@link Smudge} type by allowing user composition of overall behavior.
 * <p>
 *
 */
public abstract class SmudgeComponent extends Parametric {

	/**
	 * The universal attribute identifying a component type.
	 */
	public static final String COMPONENT_TYPE_ID_ATTR = "component-type";

	@Override
	public String getTypeCategoryName() {
		return "Smudge Component";
	}

	@Override
	public String getTypeCategoryIdentifier() {
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

	@Override
	public void save(PropertyMap pm) {
		super.save(pm);

		pm.setAttribute(COMPONENT_TYPE_ID_ATTR, getComponentTypeIdentifier());
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
