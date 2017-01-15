package io.smudgr.api.commands;

import java.io.File;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.util.ProjectLoader;
import io.smudgr.app.view.FileDialog;
import io.smudgr.app.view.FileDialog.FileDialogCallback;
import io.smudgr.app.view.FileDialog.FileDialogFilter;

/**
 * Open a project file and load it to the current application instance.
 * 
 * @see ProjectGet
 * @see ProjectNew
 * @see ProjectSave
 */
public class ProjectOpen implements ApiCommand {

	@Override
	public String getCommand() {
		return "project.open";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		FileDialogFilter filter = new FileDialogFilter("smudge", "Project files");
		FileDialog.getInstance().show("Open Smudge", false, filter, new OpenSmudgeCallback());

		return null;
	}

	private class OpenSmudgeCallback implements FileDialogCallback {

		@Override
		public void onSelection(File[] selectedFiles) {
			ProjectLoader project = new ProjectLoader(selectedFiles[0].getAbsolutePath());
			project.load();

			getController().start();
		}

		@Override
		public void onFailure(String reason) {
			System.out.println("Open smudge failed: " + reason);
		}

	}

}
