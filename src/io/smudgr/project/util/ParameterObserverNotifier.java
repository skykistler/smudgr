package io.smudgr.project.util;

import java.util.ArrayList;

import io.smudgr.project.smudge.param.Parameter;
import io.smudgr.project.smudge.param.ParameterObserver;

public class ParameterObserverNotifier {

	private ArrayList<ParameterObserver> parameterObservers = new ArrayList<ParameterObserver>();

	public synchronized void attach(ParameterObserver observer) {
		if (!parameterObservers.contains(observer))
			parameterObservers.add(observer);
	}

	public synchronized void notify(Parameter param) {
		for (ParameterObserver obsv : parameterObservers)
			obsv.parameterUpdated(param);
	}
}
