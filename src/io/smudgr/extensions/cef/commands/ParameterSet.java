package io.smudgr.extensions.cef.commands;

import java.util.HashMap;

import io.smudgr.extensions.cef.util.CefMessage;
import io.smudgr.extensions.cef.util.DebounceThread;
import io.smudgr.project.smudge.param.Parameter;
import io.smudgr.project.smudge.param.ParameterObserver;

public class ParameterSet implements CefCommand, ParameterObserver {

	private static final long PARAMETER_UPDATE_DEBOUNCE_MS = 50;

	public String getCommand() {
		return "parameter.set";
	}

	private HashMap<Parameter, DebounceThread> debounceMap = new HashMap<Parameter, DebounceThread>();

	public ParameterSet() {
		getProject().getParameterObserverNotifier().attach(this);
	}

	public CefMessage execute(CefMessage data) {
		Parameter param = (Parameter) getProject().getItem((int) data.getNumber("id"));
		param.setValue(data.get("value"));

		return null;
	}

	public void parameterUpdated(Parameter param) {
		CefMessage response = new CefMessage("id", getProject().getId(param) + "");
		response.put("value", param.getStringValue());

		// Check if this parameter has been debounced to prevent packet spam
		DebounceThread debouncer = debounceMap.get(param);
		if (debouncer == null || !debouncer.isDebouncing()) {
			// If not, send the message
			sendMessage(CefMessage.command(getCommand(), response));

			// Start a debounce to prevent another update for PARAMETER_UPDATE_DEBOUNCE_MS milliseconds
			debounceMap.put(param, DebounceThread.startDebounce(PARAMETER_UPDATE_DEBOUNCE_MS));
		}
	}

}
