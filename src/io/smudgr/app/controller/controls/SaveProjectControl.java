package io.smudgr.app.controller.controls;

import io.smudgr.app.controller.AppControl;
import io.smudgr.app.project.util.ProjectSaver;

/**
 * Save the current project to the existing project location.
 * 
 * @see ProjectSaver
 */
public class SaveProjectControl implements AppControl {

	/**
	 * @return "Save Project"
	 */
	@Override
	public String getTypeName() {
		return "Save Project";
	}

	/**
	 * Does nothing.
	 * 
	 * @see SaveProjectControl#inputOn()
	 */
	@Override
	public void inputValue(int value) {
	}

	/**
	 * Does nothing.
	 * 
	 * @see SaveProjectControl#inputOn()
	 */
	@Override
	public void inputOn() {
		ProjectSaver xml = new ProjectSaver();
		xml.save();
	}

	/**
	 * Does nothing.
	 * 
	 * @see SaveProjectControl#inputOn()
	 */
	@Override
	public void inputOff() {
	}

	/**
	 * Does nothing.
	 * 
	 * @see SaveProjectControl#inputOn()
	 */
	@Override
	public void increment() {
	}

	/**
	 * Does nothing.
	 * 
	 * @see SaveProjectControl#inputOn()
	 */
	@Override
	public void decrement() {
	}

}
