package io.smudgr.cef.controller.util;

import java.util.Vector;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefFileDialogCallback;
import org.cef.handler.CefDialogHandler;

public class DialogHandler implements CefDialogHandler {

	@Override
	public boolean onFileDialog(CefBrowser browser, FileDialogMode mode, String title, String defaultFilePath, Vector<String> acceptFilters, int selectedAcceptFilter, CefFileDialogCallback callback) {
		return false;
	}

}
