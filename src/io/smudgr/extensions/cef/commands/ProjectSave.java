package io.smudgr.extensions.cef.commands;

import java.io.File;

import io.smudgr.app.view.FileDialog;
import io.smudgr.app.view.FileDialog.FileDialogCallback;
import io.smudgr.app.view.FileDialog.FileDialogFilter;
import io.smudgr.extensions.cef.util.CefMessage;
import io.smudgr.project.ProjectSaver;

public class ProjectSave implements CefCommand {

	public String getCommand() {
		return "project.save";
	}

	public CefMessage execute(CefMessage data) {
		if (data.hasKey("as") || getProject().getProjectPath() == null) {
			showSaveAs();
		} else
			saveProject();

		return null;
	}

	private void showSaveAs() {
		FileDialogFilter filter = new FileDialogFilter("smudge", "Project files");
		FileDialog.getInstance().show("Save Smudge As", false, filter, new SaveSmudgeCallback());
	}

	private void saveProject() {
		ProjectSaver project = new ProjectSaver();
		project.save();
	}

	private class SaveSmudgeCallback implements FileDialogCallback {
		public void onSelection(File[] selectedFiles) {
			getController().getProject().setProjectPath(selectedFiles[0].getAbsolutePath());
			saveProject();
		}

		public void onFailure(String reason) {
			System.out.println("Save smudge failed: " + reason);
		}
	}

}
