package io.smudgr.extensions.cef.commands;

import io.smudgr.extensions.cef.util.CefMessage;

public interface CefCommand {

	public String getCommand();

	public CefMessage execute(CefMessage data);

}
