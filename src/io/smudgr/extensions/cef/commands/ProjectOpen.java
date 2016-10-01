package io.smudgr.extensions.cef.commands;

import java.io.File;

import io.smudgr.app.Controller;
import io.smudgr.app.view.FileDialog;
import io.smudgr.app.view.FileDialog.FileDialogCallback;
import io.smudgr.app.view.FileDialog.FileDialogFilter;
import io.smudgr.extensions.cef.util.CefMessage;
import io.smudgr.project.ProjectLoader;

public class ProjectOpen implements CefCommand {

	public String getCommand() {
		return "project.open";
	}

	public CefMessage execute(CefMessage data) {
		FileDialogFilter filter = new FileDialogFilter("smudge", "Project files");
		FileDialog.getInstance().show("Open Smudge", false, filter, new OpenSmudgeCallback());

		return null;
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
