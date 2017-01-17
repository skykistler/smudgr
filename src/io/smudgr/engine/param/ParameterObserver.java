package io.smudgr.engine.param;

import io.smudgr.app.project.Project;

/**
 * The {@link ParameterObserver} interface allows classes to become listeners
 * for changes to {@link Parameter} values.
 * 
 * @see Project#getParameterObserverNotifier()
 * @see ParameterObserverNotifier#attach(ParameterObserver)
 */
public interface ParameterObserver {

	/**
	 * This method is called on every parameter change, if the
	 * {@link ParameterObserver} is attached to a
	 * {@link ParameterObserverNotifier}
	 * 
	 * @param param
	 *            {@link Parameter} that changed
	 * 
	 * @see Project#getParameterObserverNotifier()
	 * @see ParameterObserverNotifier#attach(ParameterObserver)
	 */
	public void parameterUpdated(Parameter param);

}
