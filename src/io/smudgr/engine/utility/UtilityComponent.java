package io.smudgr.engine.utility;

import io.smudgr.engine.SmudgeComponent;
import io.smudgr.engine.param.Parametric;

/**
 * The abstract {@link UtilityComponent} class defines a generic
 * {@link Parametric} that can be added to a parent {@link Utility}.
 * <p>
 * {@link UtilityComponent} implementations will be executed by a parent
 * {@link Utility} depending on their type (as given by
 * {@link UtilityComponent#getTypeCategoryIdentifier()}), and are intended to
 * have a behavioral effect on the parent {@link Utility}
 */
public abstract class UtilityComponent extends SmudgeComponent {

	@Override
	public String getSmudgeTypeIdentifier() {
		return "utility";
	}

	@Override
	public String getTypeIdentifier() {
		return getTypeName();
	}

	@Override
	public String toString() {
		return getTypeName();
	}

	protected Utility getUtility() {
		return (Utility) getParent();
	}
}
