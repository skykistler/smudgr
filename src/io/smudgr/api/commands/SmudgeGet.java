package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.Smudge;

/**
 * Get a specific smudge model as a serialized nested JSON object.
 *
 * @see SmudgeComponentAdd
 * @see SmudgeComponentMove
 * @see SmudgeComponentRemove
 */
public class SmudgeGet implements ApiCommand {

	@Override
	public String getCommand() {
		return "smudge.get";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		int id = (int) data.getNumber("id");
		Smudge smudge = (Smudge) getProject().getItem(id);

		PropertyMap map = new PropertyMap(smudge);
		return ApiMessage.ok("smudge", ApiMessage.normalize(map));
	}

}
