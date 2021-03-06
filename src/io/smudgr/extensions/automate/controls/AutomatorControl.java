package io.smudgr.extensions.automate.controls;

import io.smudgr.app.controller.Controllable;
import io.smudgr.engine.param.Parameter;

/**
 * An {@link AutomatorControl} implements behavior for automatically interacting
 * with any given {@link Parameter}.
 * <p>
 * {@link AutomatorControl} instances are also {@link Controllable}, which
 * allows for additional user interaction with the automation behavior.
 */
public interface AutomatorControl extends Controllable {

	@Override
	public default String getTypeCategoryName() {
		return "Automator";
	}

	@Override
	public default String getTypeCategoryIdentifier() {
		return "automator";
	}

	/**
	 * This method is called when the {@link AutomatorControl} is initialized,
	 * either with the project load or on creation.
	 */
	public void init();

	/**
	 * This method is called every application update cycle.
	 */
	public void update();

	/**
	 * This method returns the {@link Parameter} that this
	 * {@link AutomatorControl} currently controls.
	 *
	 * @return {@link Parameter}
	 */
	public Parameter getParameter();
}
