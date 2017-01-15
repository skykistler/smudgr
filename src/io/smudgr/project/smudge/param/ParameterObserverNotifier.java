package io.smudgr.project.smudge.param;

import java.util.ArrayList;

public class ParameterObserverNotifier {

	private ArrayList<ParameterObserver> parameterObservers = new ArrayList<ParameterObserver>();

	public synchronized void attach(ParameterObserver observer) {
		if (!parameterObservers.contains(observer))
			parameterObservers.add(observer);
	}

	public synchronized void notify(Parameter param, ParameterObserver ignoreObserver) {
		for (ParameterObserver obsv : parameterObservers) {
			if (obsv != ignoreObserver)
				obsv.parameterUpdated(param);
		}
	}
}
