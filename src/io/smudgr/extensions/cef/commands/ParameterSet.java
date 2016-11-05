package io.smudgr.extensions.cef.commands;

import io.smudgr.app.Controller;
import io.smudgr.extensions.cef.util.CefMessage;
import io.smudgr.project.smudge.param.Parameter;

public class ParameterSet implements CefCommand {

	public String getCommand() {
		return "parameter.set";
	}

	public CefMessage execute(CefMessage data) {
		Parameter param = (Parameter) Controller.getInstance().getProject().getItem((int) data.getNumber("id"));
		param.setValue(data.get("value"));

		CefMessage response = new CefMessage("id", data.get("id"));
		response.put("value", param.getStringValue());

		return CefMessage.command(getCommand(), response);
	}

}
