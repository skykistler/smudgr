package io.smudgr.ext.cef.messages;

public interface CefMessageStrategy {

	public String getCommand();

	public boolean request(String content);

	public String onSuccess();

	public String onFailure();

}
