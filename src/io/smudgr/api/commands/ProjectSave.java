package io.smudgr.api.commands;

import java.io.File;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.util.ProjectSaver;
import io.smudgr.app.view.FileDialog;
import io.smudgr.app.view.FileDialog.FileDialogCallback;
import io.smudgr.app.view.FileDialog.FileDialogFilter;

/**
 * Save current project to user specified location.
 */
public class ProjectSave implements ApiCommand {

	@Override
	public String getCommand() {
		return "project.save";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
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

		@Override
		public void onSelection(File[] selectedFiles) {
			getController().getProject().setProjectPath(selectedFiles[0].getAbsolutePath());
			saveProject();
		}

		@Override
		public void onFailure(String reason) {
			System.out.println("Save smudge failed: " + reason);
		}

	}

}
