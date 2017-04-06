/**
 * 
 */
package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.ProjectItem;
import io.smudgr.util.source.Source;

/**
 * Set the current source with an ID
 */
public class SourceCurrentSet implements ApiCommand {

	@Override
	public String getCommand() {
		return "source.current.set";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		// Attempt to get ProjectItem at given ID
		int id = Integer.parseInt(data.get("id"));
		ProjectItem item = getProject().getItem(id);

		// Prepare response for this ID
		ApiMessage response = new ApiMessage("source", id);

		// Assert ID actually points to a Source
		if (item == null || !(item instanceof Source)) {
			response.put("message", "Did not find source with ID: " + id);
			return ApiMessage.failed(getCommand(), response);
		}

		// Get thumbnail from source
		Source source = (Source) item;

		getProject().getSourceLibrary().setSource(source);

		return null;
	}

}
