package io.smudgr.extensions.cef.commands;

import java.io.File;

import io.smudgr.app.Controller;
import io.smudgr.project.ProjectXML;
import io.smudgr.view.FileDialog;
import io.smudgr.view.FileDialog.FileDialogCallback;
import io.smudgr.view.FileDialog.FileDialogFilter;

public class OpenSmudge implements CefCommand {

	public String getCommand() {
		return "smudge.open";
	}

	public boolean request(String content) {
		FileDialogFilter filter = new FileDialogFilter("smudge", "Project files");
		FileDialog.getInstance().show("Open Smudge", false, filter, new OpenSmudgeCallback());
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

		public void onFailure(String reason) {
			System.out.println("Open smudge failed: " + reason);
		}
	}

}
