/**
 *
 */
package io.smudgr.engine;

import io.smudgr.app.project.ProjectItem;
import io.smudgr.app.project.reflect.ReflectableType;

/**
 * {@link SmudgeComponent} types diversify the configuration ability of a
 * {@link Smudge} type by allowing user composition of overall behavior.
 * <p>
 *
 */
public abstract class SmudgeComponent implements ProjectItem, ReflectableType {

	@Override
	public String getTypeName() {
		return "Smudge Component";
	}

	@Override
	public String getTypeIdentifier() {
		return "component";
	}

	/**
	 * Gets the user-recognizable component type name
	 *
	 * @return {@link String} Name of the component type this class implements
	 */
	public abstract String getComponentName();

	/**
	 * Gets the unique identifying name of the component type this class
	 * implements.
	 *
	 * @return {@link String} Component type identifier
	 */
	public abstract String getComponentIdentifier();

	/**
	 * Gets the unique identifying name of the smudge type this component
	 * is built for.
	 *
	 * @return {@link String} Smudge type identifier
	 */
	public abstract String getSmudgeIdentifier();
}
