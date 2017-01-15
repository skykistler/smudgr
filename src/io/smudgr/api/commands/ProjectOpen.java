package io.smudgr.api.commands;

import java.io.File;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.util.ProjectLoader;
import io.smudgr.app.view.FileDialog;
import io.smudgr.app.view.FileDialog.FileDialogCallback;
import io.smudgr.app.view.FileDialog.FileDialogFilter;

public class ProjectOpen implements ApiCommand {

	public String getCommand() {
		return "project.open";
	}

	public ApiMessage execute(ApiMessage data) {
		FileDialogFilter filter = new FileDialogFilter("smudge", "Project files");
		FileDialog.getInstance().show("Open Smudge", false, filter, new OpenSmudgeCallback());

		return null;
	}

	private class OpenSmudgeCallback implements FileDialogCallback {

		public void onSelection(File[] selectedFiles) {
			ProjectLoader project = new ProjectLoader(selectedFiles[0].getAbsolutePath());
			project.load();

			getController().start();
		}

		public void onFailure(String reason) {
			System.out.println("Open smudge failed: " + reason);
		}
	}

}
