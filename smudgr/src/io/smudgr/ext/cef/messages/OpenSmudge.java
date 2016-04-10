package io.smudgr.ext.cef.messages;

import java.io.File;

import io.smudgr.output.ProjectXML;
import io.smudgr.view.FileDialog;
import io.smudgr.view.FileDialog.FileDialogCallback;

public class OpenSmudge implements CefMessageStrategy {

	public String getCommand() {
		return "smudge.open";
	}

	public boolean request(String content) {
		FileDialog.getInstance().show("Open Smudge", new OpenSmudgeCallback());
		return true;
	}

	public String onSuccess() {
		return "Opening file dialog for project";
	}

	public String onFailure() {
		return "Failed to open file dialog for project";
	}

	private class OpenSmudgeCallback implements FileDialogCallback {
		public void onSelection(File[] selectedFiles) {
			ProjectXML project = new ProjectXML(selectedFiles[0].getAbsolutePath());
			project.load();
		}
	}

}
