package io.smudgr.extensions.cef.commands;

import java.io.File;

import io.smudgr.app.Controller;
import io.smudgr.app.view.FileDialog;
import io.smudgr.app.view.FileDialog.FileDialogCallback;
import io.smudgr.app.view.FileDialog.FileDialogFilter;
import io.smudgr.project.ProjectSaver;

public class SaveSmudge implements CefCommand {

	public String getCommand() {
		return "smudge.save";
	}

	public boolean request(String content) {
		if (content.equals("as") || Controller.getInstance().getProject().getProjectPath() == null) {
			showSaveAs();
		} else
			saveProject();

		return true;
	}

	private void showSaveAs() {
		FileDialogFilter filter = new FileDialogFilter("smudge", "Project files");
		FileDialog.getInstance().show("Save Smudge As", false, filter, new SaveSmudgeCallback());
	}

	private void saveProject() {
		ProjectSaver project = new ProjectSaver();
		project.save();
	}

	public String onSuccess() {
		return "Showing save dialog for project";
	}

	public String onFailure() {
		return "Failed to show save dialog for project";
	}

	private class SaveSmudgeCallback implements FileDialogCallback {
		public void onSelection(File[] selectedFiles) {
			Controller.getInstance().getProject().setProjectPath(selectedFiles[0].getAbsolutePath());
			saveProject();
		}

		public void onFailure(String reason) {
			System.out.println("Save smudge failed: " + reason);
		}
	}

}
