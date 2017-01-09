package io.smudgr.extensions.cef.commands;

import java.util.HashMap;

import io.smudgr.extensions.cef.util.CefMessage;
import io.smudgr.extensions.cef.util.DebounceCallback;
import io.smudgr.extensions.cef.util.DebounceThread;
import io.smudgr.project.smudge.param.Parameter;
import io.smudgr.project.smudge.param.ParameterObserver;

public class ParameterSet implements CefCommand, ParameterObserver {

	private static final long PARAMETER_UPDATE_DEBOUNCE_MS = 250;

	public String getCommand() {
		return "parameter.set";
	}

	private HashMap<Parameter, DebounceThread> debounceMap = new HashMap<Parameter, DebounceThread>();

	public ParameterSet() {
		getProject().getParameterObserverNotifier().attach(this);
	}

	public CefMessage execute(CefMessage data) {
		Parameter param = (Parameter) getProject().getItem((int) data.getNumber("id"));
		param.setValue(data.get("value"), this);

		return null;
	}

	public void parameterUpdated(Parameter param) {
		CefMessage response = new CefMessage("id", getProject().getId(param) + "");
		response.put("value", param.getStringValue());

		// Check if this parameter has been debounced to prevent packet spam
		DebounceThread debouncer = debounceMap.get(param);

		if (debouncer == null) {
			// If this parameter has never been debounced, send the message and schedule a debouncer
			sendMessage(CefMessage.command(getCommand(), response));

			// Start a debounce to prevent another update for PARAMETER_UPDATE_DEBOUNCE_MS milliseconds
			debouncer = new DebounceThread(PARAMETER_UPDATE_DEBOUNCE_MS);
			debouncer.start();
			debounceMap.put(param, debouncer);

		} else if (debouncer.isDebouncing()) {
			// If currently debouncing, set this update as the most recent update
			// This ensures if this parameter stops getting updated,
			// the most recent value will be sent to the front-end
			DebounceCallback callback = new DebounceCallback() {
				public void onComplete() {
					sendMessage(CefMessage.command(getCommand(), response));
				}
			};

			debouncer.setCallback(callback);
		} else {
			// If not debouncing but this parameter has been debounced before, send and debounce
			sendMessage(CefMessage.command(getCommand(), response));
			debouncer.start();
		}
	}

}
