package io.smudgr.api.commands;

import java.util.HashMap;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.engine.param.Parameter;
import io.smudgr.engine.param.ParameterObserver;
import io.smudgr.extensions.cef.util.DebounceThread;

/**
 * Set the current value of a specified parameter.
 * Takes id and value as arguments.
 * 
 * Used for both updating the back-end value from the UI, and updating the UI
 * value from the back-end.
 * 
 * @see ParameterObserver
 */
public class ParameterSet implements ApiCommand, ParameterObserver {

	@Override
	public String getCommand() {
		return "parameter.set";
	}

	private static final long PARAMETER_UPDATE_DEBOUNCE_MS = 250;
	private HashMap<Parameter, DebounceThread> debounceMap = new HashMap<Parameter, DebounceThread>();

	/**
	 * Attach this command as an observer of all parameter changes.
	 */
	public ParameterSet() {
		getProject().getParameterObserverNotifier().attach(this);
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		Parameter param = (Parameter) getProject().getItem((int) data.getNumber("id"));
		param.setValue(data.get("value"), this);

		return null;
	}

	@Override
	public void parameterUpdated(Parameter param) {
		/*
		 * Check if this parameter has been debounced to prevent packet spam
		 */
		DebounceThread debouncer = debounceMap.get(param);

		if (debouncer == null) {
			/*
			 * If this parameter has never been debounced, send the message and
			 * schedule a debouncer
			 */
			sendParameterUpdate(param);

			/*
			 * Start a debounce thread to prevent another update for
			 * PARAMETER_UPDATE_DEBOUNCE_MS milliseconds
			 */
			debouncer = new DebounceThread(PARAMETER_UPDATE_DEBOUNCE_MS);
			debouncer.start();
			debounceMap.put(param, debouncer);

		} else if (debouncer.isDebouncing()) {
			/*
			 * TODO: Code currently disabled until we have better front-end
			 * knobs to test this with.
			 */

			// /*
			// * If currently debouncing, set a callback to
			// sendParameterUpdate()
			// * on completion.
			// *
			// * This ensures that if this parameter has stopped being updated,
			// * the most recent value will still be sent to the front-end
			// */
			// DebounceCallback callback = new DebounceCallback() {
			// @Override
			// public void onComplete() {
			// sendParameterUpdate(param);
			// }
			// };
			//
			// debouncer.setCallback(callback);
		} else {
			/*
			 * If not debouncing but this parameter the debouncer exists,
			 * send and debounce.
			 */
			sendParameterUpdate(param);
			debouncer.start();
		}
	}

	private void sendParameterUpdate(Parameter param) {
		ApiMessage response = new ApiMessage("id", getProject().getId(param) + "");
		response.put("value", param.getStringValue());

		sendMessage(ApiMessage.ok(getCommand(), response));
	}

}
