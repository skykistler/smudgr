package io.smudgr.engine.param;

import java.util.ArrayList;

import io.smudgr.app.project.Project;

/**
 * The {@link ParameterObserverNotifier} class tracks and notifies
 * {@link ParameterObserver} observers of {@link Parameter} changes.
 * 
 * @see Project#getParameterObserverNotifier()
 */
public class ParameterObserverNotifier {

	private ArrayList<ParameterObserver> parameterObservers = new ArrayList<ParameterObserver>();

	/**
	 * Add a {@link ParameterObserver} to notify of {@link Parameter} changes.
	 * 
	 * @param observer
	 *            {@link ParameterObserver}
	 * 
	 * @see Project#getParameterObserverNotifier()
	 */
	public synchronized void attach(ParameterObserver observer) {
		if (!parameterObservers.contains(observer))
			parameterObservers.add(observer);
	}

	/**
	 * Notifies all attached {@link ParameterObserver} instances of
	 * {@link Parameter} changes.
	 * <p>
	 * Optionally ignore a specific {@link ParameterObserver} in case the
	 * observer is the source that changed the {@link Parameter}. This avoids
	 * infinite loops.
	 * 
	 * @param param
	 *            {@link Parameter}
	 * @param ignoreObserver
	 *            {@link ParameterObserver}
	 * 
	 * @see Project#getParameterObserverNotifier()
	 */
	public synchronized void notify(Parameter param, ParameterObserver ignoreObserver) {
		for (ParameterObserver obsv : parameterObservers) {
			if (obsv != ignoreObserver)
				obsv.parameterUpdated(param);
		}
	}
}
