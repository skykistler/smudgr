package io.smudgr.extensions.cef.commands;

import io.smudgr.extensions.cef.util.CefMessage;
import io.smudgr.project.smudge.param.Parameter;
import io.smudgr.project.smudge.param.ParameterObserver;

public class ParameterSet implements CefCommand, ParameterObserver {

	public String getCommand() {
		return "parameter.set";
	}

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

		sendMessage(CefMessage.command(getCommand(), response));
	}

}
