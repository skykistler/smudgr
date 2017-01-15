package io.smudgr.app.controller.controls;

import io.smudgr.app.controller.AppControl;
import io.smudgr.app.controller.Controller;
import io.smudgr.util.source.Source;
import io.smudgr.util.source.SourceSet;

/**
 * Change the current {@link Source} within the current {@link SourceSet}
 * 
 * @see SourceLibraryControl
 */
public class CurrentSourceControl implements AppControl {

	/**
	 * @return "Source Switcher"
	 */
	@Override
	public String getName() {
		return "Source Switcher";
	}

	/**
	 * Does nothing.
	 * 
	 * @see CurrentSourceControl#increment()
	 * @see CurrentSourceControl#decrement()
	 */
	@Override
	public void inputValue(int value) {
	}

	/**
	 * Does nothing.
	 * 
	 * @see CurrentSourceControl#increment()
	 * @see CurrentSourceControl#decrement()
	 */
	@Override
	public void inputOn() {
	}

	/**
	 * Does nothing.
	 * 
	 * @see CurrentSourceControl#increment()
	 * @see CurrentSourceControl#decrement()
	 */
	@Override
	public void inputOff() {
	}

	/**
	 * Switch to the next {@link Source} within the current {@link SourceSet}
	 * 
	 * @see CurrentSourceControl#decrement()
	 */
	@Override
	public void increment() {
		Source src = Controller.getInstance().getProject().getSmudge().getSource();

		if (src instanceof SourceSet)
			((SourceSet) src).nextSource();
	}

	/**
	 * Switch to the previous {@link Source} within the current
	 * {@link SourceSet}
	 * 
	 * @see CurrentSourceControl#increment()
	 */
	@Override
	public void decrement() {
		Source src = Controller.getInstance().getProject().getSmudge().getSource();

		if (src instanceof SourceSet)
			((SourceSet) src).previousSource();
	}

}
