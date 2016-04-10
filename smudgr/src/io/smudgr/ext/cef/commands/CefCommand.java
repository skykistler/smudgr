package io.smudgr.ext.cef.commands;

public interface CefCommand {

	public String getCommand();

	public boolean request(String content);

	public String onSuccess();

	public String onFailure();

}
