package io.smudgr.app.controller;

import io.smudgr.app.project.reflect.ReflectableType;

/**
 * Interface for defining controllable app behavior and/or actions.
 *
 * @see Controllable
 */
public interface AppControl extends Controllable, ReflectableType {

	@Override
	public default String getTypeIdentifier() {
		return "control";
	}

	@Override
	public default String getTypeName() {
		return "App Control";
	}

	@Override
	public default String getIdentifier() {
		return getName();
	}
}
