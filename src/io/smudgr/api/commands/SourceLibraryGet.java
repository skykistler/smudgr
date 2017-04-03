package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.util.PropertyMap;

/**
 * Gets a list of source sets and sources contained in them.
 */
public class SourceLibraryGet implements ApiCommand {

	@Override
	public String getCommand() {
		return "source.library.get";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		PropertyMap sourceLibrary = new PropertyMap("source-library");
		getProject().getSourceLibrary().save(sourceLibrary);

		return ApiMessage.ok("source.library", ApiMessage.normalize(sourceLibrary));
	}

}
