package io.smudgr.extensions.cef.commands;

import java.io.File;

import io.smudgr.app.Controller;
import io.smudgr.app.view.FileDialog;
import io.smudgr.app.view.FileDialog.FileDialogCallback;
import io.smudgr.app.view.FileDialog.FileDialogFilter;
import io.smudgr.project.ProjectLoader;

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
		return "Showing open dialog for project";
	}

	public String onFailure() {
		return "Failed to show open dialog for project";
	}

	private class OpenSmudgeCallback implements FileDialogCallback {

		public void onSelection(File[] selectedFiles) {
			ProjectLoader project = new ProjectLoader(selectedFiles[0].getAbsolutePath());
			project.load();

			Controller.getInstance().start();
		}

		public void onFailure(String reason) {
			System.out.println("Open smudge failed: " + reason);
		}
	}

}
