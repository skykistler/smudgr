package io.smudgr.ext.cef.commands;

import java.io.File;

import io.smudgr.app.ProjectXML;
import io.smudgr.controller.Controller;
import io.smudgr.view.FileDialog;
import io.smudgr.view.FileDialog.FileDialogCallback;

public class OpenSmudge implements CefCommand {

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

			Controller.getInstance().start();
		}
	}

}
