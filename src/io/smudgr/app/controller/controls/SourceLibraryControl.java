package io.smudgr.app.controller.controls;

import io.smudgr.app.controller.AppControl;
import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.SourceLibrary;
import io.smudgr.util.source.SourceSet;

/**
 * Change the current {@link SourceSet} within the current {@link SourceLibrary}
 * 
 * @see CurrentSourceControl
 */
public class SourceLibraryControl implements AppControl {

	/**
	 * @return "Source Set Switcher"
	 */
	@Override
	public String getTypeName() {
		return "Source Set Switcher";
	}

	/**
	 * Does nothing.
	 * 
	 * @see SourceLibraryControl#increment()
	 */
	@Override
	public void inputValue(int value) {
	}

	/**
	 * Does nothing.
	 * 
	 * @see SourceLibraryControl#increment()
	 */
	@Override
	public void inputOn() {
	}

	/**
	 * Does nothing.
	 * 
	 * @see SourceLibraryControl#increment()
	 */
	@Override
	public void inputOff() {
	}

	/**
	 * Switch to the next {@link SourceSet} within the current
	 * {@link SourceLibrary}
	 * 
	 * @see SourceLibraryControl#decrement()
	 */
	@Override
	public void increment() {
		Controller.getInstance().getProject().getSourceLibrary().nextSet();
	}

	/**
	 * Switch to the previous {@link SourceSet} within the current
	 * {@link SourceLibrary}
	 * 
	 * @see SourceLibraryControl#increment()
	 */
	@Override
	public void decrement() {
		Controller.getInstance().getProject().getSourceLibrary().previousSet();
	}

}
