package io.smudgr.api.commands;

import java.io.File;
import java.util.ArrayList;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;

/**
 * Gets a list of files and folders in a given path.
 * Path must be a directory or this command will fail.
 */
public class FileList implements ApiCommand {

	@Override
	public String getCommand() {
		return "file.list";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		String path = data.get("path");
		File folder = new File(path);

		File[] listFiles = folder.listFiles();

		if (listFiles == null)
			return ApiMessage.failed(getCommand(), new ApiMessage("message", "Not a directory or does not exist: " + path));

		ArrayList<String> files = new ArrayList<String>();
		ArrayList<String> folders = new ArrayList<String>();

		for (File file : listFiles) {
			String filename = file.getName();

			if (file.isDirectory()) {
				folders.add(filename);
			} else {
				files.add(filename);
			}
		}

		ApiMessage response = new ApiMessage();
		response.put("files", files);
		response.put("folders", folders);

		return ApiMessage.success("file.list", response);
	}

}
