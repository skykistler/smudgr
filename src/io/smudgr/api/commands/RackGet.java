package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.Rack;

/**
 * Get a specific rack model as a serialized nested JSON object.
 *
 * @see RackSmudgeAdd
 * @see RackSmudgeMove
 * @see RackSmudgeRemove
 */
public class RackGet implements ApiCommand {

	@Override
	public String getCommand() {
		return "rack.get";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		int id = (int) data.getNumber("id");
		Rack rack = (Rack) getProject().getItem(id);

		PropertyMap map = new PropertyMap(rack);
		return ApiMessage.ok("rack", ApiMessage.normalize(map));
	}

}
